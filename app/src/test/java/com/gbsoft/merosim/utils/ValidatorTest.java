/*
 * Created by Chiranjeevi Pandey on 2/28/22, 2:50 PM
 * Copyright (c) 2022. Some rights reserved.
 * Last modified: 2022/02/28
 *
 * Licensed under GNU General Public License v3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gbsoft.merosim.utils;

import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {
    @Test
    public void isFullNameValid() {
        String name1 = "Chiranjeevi Pandey";
        String name2 = "Samir K.C.";
        String name3 = "Sabina-Neupane";
        String name4 = "Suraj24";
        Assert.assertTrue(Validator.isFullNameValid(name1));
        Assert.assertTrue(Validator.isFullNameValid(name2));
        Assert.assertFalse(Validator.isFullNameValid(name3));
        Assert.assertFalse(Validator.isFullNameValid(name4));
    }
}