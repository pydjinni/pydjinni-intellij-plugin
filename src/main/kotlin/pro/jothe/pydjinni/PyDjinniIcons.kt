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

import com.intellij.openapi.util.IconLoader.getIcon
import javax.swing.Icon

object PyDjinniIcons {
    val FILE: Icon = getIcon("/icons/file_icon.svg", PyDjinniIcons::class.java)
    val LSP: Icon = getIcon("/icons/lsp_icon.svg", PyDjinniIcons::class.java)
}
