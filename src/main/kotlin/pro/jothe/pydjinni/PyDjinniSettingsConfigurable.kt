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

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*


class PyDjinniSettingsConfigurable(private val project: Project
) : BoundSearchableConfigurable("PyDjinni", "PyDjinni") {

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                textFieldWithBrowseButton(
                    browseDialogTitle = "Configuration File",
                    project = project
                )
                .label("Configuration file:", LabelPosition.TOP)
                .bindText(configurationState::configurationFile.toNonNullableProperty(""))
                .comment("The configuration file is used to configure the language server.<br>A JSON-Schema will be applied to the configured file.")
                .align(Align.FILL)
            }
            row {
                checkBox("Generate on save").bindSelected(configurationState::generateOnSave)
                    .comment("If enabled, the PyDjinni generator will run on file save.<br><i>This will do a clean regeneration, which might lead to unexcepted behavior if multiple pydjinni files are chained with <code>@import</code></i>!")
            }
            row {
                textFieldWithBrowseButton(
                    browseDialogTitle = "Generate base path",
                    project = project
                )
                    .label("Generate base path:", LabelPosition.TOP)
                    .bindText(configurationState::generateBasePath.toNonNullableProperty(""))
                    .comment("Base path for the generated files.<br>If empty, the files will be generated relative to the project root.")
                    .align(Align.FILL)
            }
            group("Debugging") {
                row {
                    checkBox("Enable language server logs").bindSelected(configurationState::enableLanguagesServerLogs)
                        .comment("If enabled, the language server logs will be written to <code>pydjinni_lsp.log</code>")
                }

            }
        }
    }

    override fun apply() {
        super.apply()
        reloadLsp(project)
        reloadConfigurationSchema(project)
    }

    private val configurationState: PyDjinniConfigurationState
        get() = project.getService(PyDjinniConfigurationStateSettings::class.java).state

}
