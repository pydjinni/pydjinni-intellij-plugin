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
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import com.jetbrains.python.configuration.PyActiveSdkModuleConfigurable
import com.jetbrains.python.packaging.common.PythonSimplePackageSpecification
import com.jetbrains.python.packaging.management.PythonPackageManager
import com.jetbrains.python.sdk.PythonSdkUtil
import kotlinx.coroutines.runBlocking
import java.util.function.Function
import javax.swing.JComponent

class EditorNotificationProvider : EditorNotificationProvider {
    private fun createNotification(project: Project, editor: FileEditor, file: VirtualFile): EditorNotificationPanel? {
        if(file.extension == PYDJINNI_FILE_TYPE_EXTENSION ||
            file.name == project.getService(PyDjinniConfigurationStateSettings::class.java).state.configurationFile) {
            val sdk = PythonSdkUtil.findPythonSdk(project.modules[0])
            if(sdk == null) {
                val panel = EditorNotificationPanel(editor, EditorNotificationPanel.Status.Warning)
                panel.text = "No Python interpreter configured for the project"
                panel.toolTipText = "The PyDjinni Plugin requires Python for running the PyDjinni language server. " +
                        "Make sure that a Python interpreter is configured and that the PyDjinni Python package is installed."
                panel.createActionLabel("Configure Python interpreter") {
                    ShowSettingsUtil.getInstance()
                        .showSettingsDialog(project, PyActiveSdkModuleConfigurable::class.java)
                }
                return panel
            } else {
                val cmd = GeneralCommandLine(sdk.homePath, "-m", "pydjinni_language_server", "--version")
                cmd.setWorkDirectory(project.basePath)
                val result = cmd.toProcessBuilder().redirectOutput(ProcessBuilder.Redirect.PIPE).start().waitFor()
                if (result != 0) {
                    val panel = EditorNotificationPanel(editor, EditorNotificationPanel.Status.Warning)
                    panel.text = "PyDjinni Languages Server could not be found"
                    panel.toolTipText = "The PyDjinni Plugin requires the PyDjinni Python package to be installed in the configured Python environment."
                    panel.createActionLabel("Install PyDjinni") {
                        runBackgroundableTask("Installing Pydjinni", project) {
                            runBlocking {
                                PythonPackageManager.forSdk(project, sdk).installPackage(
                                    PythonSimplePackageSpecification(
                                        name = "pydjinni", version = null, repository = null
                                    )
                                )
                            }
                        }
                    }
                    return panel
                }
            }
        }
        return null
    }

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?> {
        return Function {editor -> createNotification(project, editor, file)}
    }

    override fun isDumbAware(): Boolean {
        return true
    }
}
