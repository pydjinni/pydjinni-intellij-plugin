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
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import com.jetbrains.python.sdk.PythonSdkUtil

class PyDjinniLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerSupportProvider.LspServerStarter
    ) {
        if(file.extension == PYDJINNI_FILE_TYPE_EXTENSION) {
            serverStarter.ensureServerStarted(PyDjinniLspServerDescriptor(project))
        }
    }

    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        LspServerWidgetItem(lspServer, currentFile,
            PyDjinniIcons.LSP, PyDjinniSettingsConfigurable::class.java)
}

private class PyDjinniLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "PyDjinni") {
    override fun isSupportedFile(file: VirtualFile) = file.extension == PYDJINNI_FILE_TYPE_EXTENSION

    private val configurationState: PyDjinniConfigurationState
        get() = project.getService(PyDjinniConfigurationStateSettings::class.java).state

    override fun createCommandLine(): GeneralCommandLine {
        val sdk = PythonSdkUtil.findPythonSdk(project.modules[0])
        val params = arrayListOf("--connection", "STDIO", "--config", configurationState.configurationFile)
        if (configurationState.enableLanguagesServerLogs) {
            params.addAll(arrayListOf("--log", "pydjinni_lsp.log"))
        }
        val cmd = GeneralCommandLine(sdk?.homePath, "-m", "pydjinni_language_server", "start", *(params.toTypedArray()))
        cmd.setWorkDirectory(project.basePath)
        return cmd
    }
}
