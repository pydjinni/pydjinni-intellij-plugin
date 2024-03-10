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

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*


class PyDjinniSettingsConfigurable(private val project: Project) : BoundConfigurable("PyDjinni") {

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                textFieldWithBrowseButton(
                    browseDialogTitle = "Configuration File",
                    project = project,
                    fileChooserDescriptor = FileChooserDescriptor(true, false, false, false, false, false)
                )
                .label("Configuration file:", LabelPosition.TOP)
                .bindText(configurationState::configurationFile)
                .comment("The configuration file is used to configure the language server.<br>A JSON-Schema will be applied to the configured file.")
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
        get() = project.getService(PyDjinniConfigurationState::class.java)
}
