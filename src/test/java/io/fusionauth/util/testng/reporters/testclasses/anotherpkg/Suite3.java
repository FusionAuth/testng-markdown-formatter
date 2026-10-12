/*
 * Copyright (c) 2025, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package io.fusionauth.util.testng.reporters.testclasses.anotherpkg;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class Suite3 {
  @Test
  void a_passing_test2() {
    Assert.assertTrue(true);
  }

  @Test
  void another_passing_test2() {
    Assert.assertTrue(true);
  }

  @Test(enabled = false)
  void disabled_test2() {
  }

  @Test
  void failing_test2() {
    Assert.assertTrue(false);
  }

  @Test
  void skipped_test2() {
    throw new SkipException("skipped");
  }
}
