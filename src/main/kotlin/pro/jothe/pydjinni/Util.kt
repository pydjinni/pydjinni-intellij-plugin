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
import com.intellij.platform.lsp.api.LspServerState
import com.jetbrains.jsonSchema.ide.JsonSchemaService
import com.jetbrains.jsonSchema.impl.JsonSchemaCacheManager

fun reloadLsp(project: Project) {
    val manager = LspServerManager.getInstance(project)
    val servers = manager.getServersForProvider(PyDjinniLspServerSupportProvider::class.java)
    if(!servers.any { it.state == LspServerState.Initializing }) {
        manager.stopAndRestartIfNeeded(PyDjinniLspServerSupportProvider::class.java)
    }
    JsonSchemaCacheManager.getInstance(project).dispose()
}

fun reloadConfigurationSchema(project: Project) {
    val schemaService = project.getService(JsonSchemaService::class.java)
    schemaService.reset()
}