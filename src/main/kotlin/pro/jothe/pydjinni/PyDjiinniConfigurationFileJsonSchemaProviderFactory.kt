// Copyright 2024 jothepro
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package pro.jothe.pydjinni

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.python.sdk.PythonSdkUtil

class PyDjiinniConfigurationFileJsonSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): MutableList<JsonSchemaFileProvider> {
        val sdk = PythonSdkUtil.findPythonSdk(project.modules[0])
        if(sdk == null) {
            return arrayListOf()
        } else {
            val cmd = GeneralCommandLine(sdk.homePath, "-m", "pydjinni_language_server", "config-schema")
            cmd.setWorkDirectory(project.basePath)
            val process = cmd.toProcessBuilder().redirectOutput(ProcessBuilder.Redirect.PIPE).start()
            val output = process.inputReader().use { it.readText() }
            if(output == "") {
                return arrayListOf()
            } else {
                return arrayListOf(object : JsonSchemaFileProvider {

                    val virtualFile: VirtualFile = LightVirtualFile("pydjinni_configuration.schema.json", output)

                    override fun isAvailable(file: VirtualFile): Boolean {
                        return file.name == project.getService(PyDjinniConfigurationState::class.java).configurationFile
                    }

                    override fun getName(): String {
                        return "PyDjinni Configuration"
                    }

                    override fun getSchemaFile(): VirtualFile {
                        return virtualFile
                    }

                    override fun getSchemaType(): SchemaType {
                        return SchemaType.embeddedSchema
                    }

                })
            }

        }
    }

}
