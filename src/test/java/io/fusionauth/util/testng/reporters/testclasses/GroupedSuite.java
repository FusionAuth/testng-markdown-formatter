package io.fusionauth.util.testng.reporters.testclasses;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GroupedSuite {
  @Test(groups = "group1")
  public void failing_test_group1() {
    Assert.assertTrue(false);
  }

  @Test(groups = "group2")
  public void failing_test_group2() {
    Assert.assertTrue(false);
  }
}
