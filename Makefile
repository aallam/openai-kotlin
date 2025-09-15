build-maven-local:
	@echo "Building and publishing JARs to local Maven repository (.maven)..."
	./gradlew :openai-core:publishJvmPublicationToMavenRepository :openai-core:publishKotlinMultiplatformPublicationToMavenRepository -PRELEASE_SIGNING_ENABLED=false
	./gradlew :openai-client:publishJvmPublicationToMavenRepository :openai-client:publishKotlinMultiplatformPublicationToMavenRepository -PRELEASE_SIGNING_ENABLED=false
	./gradlew :openai-client-bom:publishMavenPublicationToMavenRepository -PRELEASE_SIGNING_ENABLED=false
	@echo "Local JARs published to .maven directory"

generate-maven-dependencies:
	@echo "Generating Maven dependencies report..."
	./gradlew dependencies --configuration runtimeClasspath > maven-dependencies.txt 2>&1 || true
	./gradlew :openai-core:dependencies --configuration jvmRuntimeClasspath >> maven-dependencies.txt 2>&1 || true
	./gradlew :openai-client:dependencies --configuration jvmRuntimeClasspath >> maven-dependencies.txt 2>&1 || true
	@echo "Maven dependencies report generated in maven-dependencies.txt"

generate-maven-pom:
	@echo "Generating Maven POM files..."
	./gradlew :openai-core:generatePomFileForJvmPublication :openai-core:generatePomFileForKotlinMultiplatformPublication
	./gradlew :openai-client:generatePomFileForJvmPublication :openai-client:generatePomFileForKotlinMultiplatformPublication
	./gradlew :openai-client-bom:generatePomFileForMavenPublication
	@echo "Maven POM files generated in build/publications/"

generate-maven-all: build-maven-local generate-maven-dependencies generate-maven-pom
	@echo "All Maven artifacts and dependencies generated successfully"

.PHONY: build-maven-local generate-maven-dependencies generate-maven-pom generate-maven-all