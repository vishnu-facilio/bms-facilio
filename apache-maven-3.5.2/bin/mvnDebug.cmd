@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.

@REM -----------------------------------------------------------------------------
@REM Apache Maven Debug Script
@REM
@REM Environment Variable Prerequisites
@REM
@REM   JAVA_HOME          Must point at your Java Development Kit installation.
@REM   MAVEN_BATCH_ECHO  (Optional) Set to 'on' to enable the echoing of the batch commands.
@REM   MAVEN_BATCH_PAUSE (Optional) set to 'on' to wait for a key stroke before ending.
@REM   MAVEN_OPTS        (Optional) Java runtime options used when Maven is executed.
@REM   MAVEN_SKIP_RC     (Optional) Flag to disable loading of mavenrc files.
@REM -----------------------------------------------------------------------------

@setlocal
@set MAVEN_DEBUG_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000

@call "%~dp0"mvn.cmd %*
