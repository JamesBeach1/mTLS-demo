tasks.register("generateCertsForAll") {
    description = "Generate certificates for all subprojects"
    group = "certificates"

    doLast {
        println("Generating certificates for mTLS demo...")

        subprojects.forEach { project ->
            val certsDir = file("${project.projectDir}/src/main/resources/certs")
            certsDir.mkdirs()
        }

        generateCertificates()
    }
}

fun generateCertificates() {
    val rootCertsDir = file("shared-certs")
    rootCertsDir.mkdirs()

    if (!file("${rootCertsDir}/truststore.p12").exists()) {
        println("Generating new certificates...")

        exec { commandLine("openssl", "genrsa", "-out", "${rootCertsDir}/ca-key.pem", "4096") }
        exec {
            commandLine("openssl", "req", "-new", "-x509", "-days", "365",
                    "-key", "${rootCertsDir}/ca-key.pem", "-out", "${rootCertsDir}/ca-cert.pem",
                    "-subj", "/CN=Demo-CA/O=Demo/C=US")
        }

        exec { commandLine("openssl", "genrsa", "-out", "${rootCertsDir}/service-a-key.pem", "4096") }
        exec {
            commandLine("openssl", "req", "-new", "-key", "${rootCertsDir}/service-a-key.pem",
                    "-out", "${rootCertsDir}/service-a.csr", "-subj", "/CN=localhost/O=Demo/C=US")
        }
        exec {
            commandLine("openssl", "x509", "-req", "-days", "365", "-in", "${rootCertsDir}/service-a.csr",
                    "-CA", "${rootCertsDir}/ca-cert.pem", "-CAkey", "${rootCertsDir}/ca-key.pem",
                    "-CAcreateserial", "-out", "${rootCertsDir}/service-a-cert.pem")
        }

        exec { commandLine("openssl", "genrsa", "-out", "${rootCertsDir}/service-b-key.pem", "4096") }
        exec {
            commandLine("openssl", "req", "-new", "-key", "${rootCertsDir}/service-b-key.pem",
                    "-out", "${rootCertsDir}/service-b.csr", "-subj", "/CN=localhost/O=Demo/C=US")
        }
        exec {
            commandLine("openssl", "x509", "-req", "-days", "365", "-in", "${rootCertsDir}/service-b.csr",
                    "-CA", "${rootCertsDir}/ca-cert.pem", "-CAkey", "${rootCertsDir}/ca-key.pem",
                    "-CAcreateserial", "-out", "${rootCertsDir}/service-b-cert.pem")
        }

        exec {
            commandLine("openssl", "pkcs12", "-export", "-in", "${rootCertsDir}/service-a-cert.pem",
                    "-inkey", "${rootCertsDir}/service-a-key.pem", "-out", "${rootCertsDir}/service-a-keystore.p12",
                    "-name", "service-a", "-passout", "pass:changeit")
        }
        exec {
            commandLine("openssl", "pkcs12", "-export", "-in", "${rootCertsDir}/service-b-cert.pem",
                    "-inkey", "${rootCertsDir}/service-b-key.pem", "-out", "${rootCertsDir}/service-b-keystore.p12",
                    "-name", "service-b", "-passout", "pass:changeit")
        }

        exec {
            commandLine("keytool", "-import", "-trustcacerts", "-noprompt", "-alias", "demo-ca",
                    "-file", "${rootCertsDir}/ca-cert.pem", "-keystore", "${rootCertsDir}/truststore.p12",
                    "-storetype", "PKCS12", "-storepass", "changeit")
        }

        subprojects.forEach { project ->
            val projectCertsDir = file("${project.projectDir}/src/main/resources/certs")
            projectCertsDir.mkdirs()

            copy {
                from(rootCertsDir)
                into(projectCertsDir)
                include("*.p12")
            }

            when (project.name) {
                "project-A" -> {
                    copy {
                        from("${rootCertsDir}/service-a-keystore.p12")
                        into(projectCertsDir)
                    }
                }
                "project-B" -> {
                    copy {
                        from("${rootCertsDir}/service-b-keystore.p12")
                        into(projectCertsDir)
                    }
                }
            }
        }

        delete("${rootCertsDir}/service-a.csr", "${rootCertsDir}/service-b.csr", "${rootCertsDir}/ca-cert.srl")

        println("Certificates generated and distributed to subprojects")
    } else {
        println("Certificates already exist, skipping generation")
    }
}

tasks.register("cleanCerts") {
    description = "Clean all generated certificates"
    group = "certificates"

    doLast {
        delete("shared-certs")
        subprojects.forEach { project ->
            delete("${project.projectDir}/src/main/resources/certs")
        }
        println("All certificates cleaned")
    }
}

tasks.register("buildAll") {
    description = "Build all subprojects"
    group = "build"
    dependsOn(subprojects.map { it.tasks.named("build") })
}

tasks.register("runAll") {
    description = "Run all Spring Boot applications"
    group = "application"
    dependsOn(subprojects.map { "${it.name}:bootRun" })
}