.PHONY: $(shell egrep -o ^[a-zA-Z_-]+: $(MAKEFILE_LIST) | sed 's/://')

asm:
	@./gradlew assemble --warning-mode all

build:
	@./gradlew installDebug --warning-mode all

dev:
	@make build && make run

run:
	@adb shell am start -n com.ec22s.lightsensor/.MainActivity
