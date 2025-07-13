@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  bot-app startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and BOT_APP_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\bot-app-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\audio-lavaplayer-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\bot-core-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\audio-lavalink-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\infra-config-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\infra-metrics-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\infra-cache-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\JDA-5.5.1.jar;%APP_HOME%\lib\logback-classic-1.4.14.jar;%APP_HOME%\lib\lavaplayer-2.2.3.jar;%APP_HOME%\lib\audio-common-1.0.0-SNAPSHOT.jar;%APP_HOME%\lib\resilience4j-circuitbreaker-2.2.0.jar;%APP_HOME%\lib\sqlite-jdbc-3.45.1.0.jar;%APP_HOME%\lib\lava-common-2.2.3.jar;%APP_HOME%\lib\resilience4j-core-2.2.0.jar;%APP_HOME%\lib\slf4j-api-2.0.17.jar;%APP_HOME%\lib\dagger-2.48.1.jar;%APP_HOME%\lib\jackson-core-2.18.3.jar;%APP_HOME%\lib\jackson-annotations-2.18.3.jar;%APP_HOME%\lib\jackson-databind-2.18.3.jar;%APP_HOME%\lib\v2-1.8.0.jar;%APP_HOME%\lib\okhttp-4.12.0.jar;%APP_HOME%\lib\config-1.4.3.jar;%APP_HOME%\lib\micrometer-registry-prometheus-1.12.1.jar;%APP_HOME%\lib\core-3.1.0.jar;%APP_HOME%\lib\tink-1.17.0.jar;%APP_HOME%\lib\nv-websocket-client-2.14.jar;%APP_HOME%\lib\opus-java-1.1.1.jar;%APP_HOME%\lib\commons-collections4-4.4.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\logback-core-1.4.14.jar;%APP_HOME%\lib\lavaplayer-natives-2.2.3.jar;%APP_HOME%\lib\common-1.8.0.jar;%APP_HOME%\lib\rhino-engine-1.7.15.jar;%APP_HOME%\lib\commons-io-2.13.0.jar;%APP_HOME%\lib\jsoup-1.16.1.jar;%APP_HOME%\lib\base64-2.3.9.jar;%APP_HOME%\lib\json-20240303.jar;%APP_HOME%\lib\protocol-jvm-4.0.7.jar;%APP_HOME%\lib\kotlinx-serialization-core-jvm-1.7.0.jar;%APP_HOME%\lib\kotlinx-serialization-json-jvm-1.7.0.jar;%APP_HOME%\lib\kotlinx-datetime-jvm-0.6.0.jar;%APP_HOME%\lib\okio-jvm-3.6.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.9.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.9.10.jar;%APP_HOME%\lib\kotlin-stdlib-2.0.0.jar;%APP_HOME%\lib\annotations-24.0.0.jar;%APP_HOME%\lib\httpclient-4.5.14.jar;%APP_HOME%\lib\nanojson-1.7.jar;%APP_HOME%\lib\micrometer-core-1.12.1.jar;%APP_HOME%\lib\simpleclient_common-0.16.0.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\gson-2.10.1.jar;%APP_HOME%\lib\error_prone_annotations-2.22.0.jar;%APP_HOME%\lib\protobuf-java-4.28.2.jar;%APP_HOME%\lib\opus-java-api-1.1.1.jar;%APP_HOME%\lib\opus-java-natives-1.1.1.jar;%APP_HOME%\lib\httpcore-4.4.16.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\rhino-1.7.15.jar;%APP_HOME%\lib\micrometer-observation-1.12.1.jar;%APP_HOME%\lib\micrometer-commons-1.12.1.jar;%APP_HOME%\lib\HdrHistogram-2.1.12.jar;%APP_HOME%\lib\LatencyUtils-2.0.3.jar;%APP_HOME%\lib\simpleclient-0.16.0.jar;%APP_HOME%\lib\jna-4.4.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel-0.16.0.jar;%APP_HOME%\lib\simpleclient_tracer_otel_agent-0.16.0.jar;%APP_HOME%\lib\simpleclient_tracer_common-0.16.0.jar


@rem Execute bot-app
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %BOT_APP_OPTS%  -classpath "%CLASSPATH%" dev.cafe.bot.Main %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable BOT_APP_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%BOT_APP_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
