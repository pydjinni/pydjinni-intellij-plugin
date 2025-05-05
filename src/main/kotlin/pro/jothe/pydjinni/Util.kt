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

import com.intellij.openapi.project.Project
import com.intellij.platform.lsp.api.LspServerManager
import com.intellij.ui.EditorNotifications
import com.jetbrains.jsonSchema.ide.JsonSchemaService
import org.eclipse.lsp4j.DidChangeConfigurationParams

fun Project.notifyConfigurationChange() {
    LspServerManager.getInstance(this).getServersForProvider(PyDjinniLspServerSupportProvider::class.java).forEach { server ->
        server.sendNotification { it.workspaceService.didChangeConfiguration(DidChangeConfigurationParams(emptyArray<Any>())) }
    }
}

fun Project.restartServer() {
    LspServerManager.getInstance(this).apply {
        stopAndRestartIfNeeded(PyDjinniLspServerSupportProvider::class.java)
    }
}

fun Project.reloadConfigurationSchema() {
    getService(JsonSchemaService::class.java).apply {
        reset()
    }
}

fun Project.updateAllNotifications() {
    EditorNotifications.getInstance(this).updateAllNotifications()
}
