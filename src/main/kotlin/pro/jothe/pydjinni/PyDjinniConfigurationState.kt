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

import com.intellij.openapi.components.*

@Service(Service.Level.PROJECT)
@State(
    name = "pro.jothe.pydjinni.PyDjinniConfigurationStateSettings",
    storages = [Storage("pydjinni.xml")],
)
class PyDjinniConfigurationStateSettings : SimplePersistentStateComponent<PyDjinniConfigurationState>(PyDjinniConfigurationState())

class PyDjinniConfigurationState : BaseState() {
    companion object {
        const val DEFAULT_CONFIGURATION_FILE: String = "pydjinni.yaml"
    }

    var configurationFile by string(DEFAULT_CONFIGURATION_FILE)
    var enableLanguagesServerLogs by property(false)
    var generateOnSave: Boolean by property(false)
}
