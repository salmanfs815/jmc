package org.openjdk.jmc.flightrecorder.ui.views.stacktrace;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceFrame;

public class AllocationPressureTest {

	@Test
	public void allocationPressureCalculationTest() {

		StacktraceFrame stFrame= mock(StacktraceFrame.class);
		// TODO: mock methods of StacktraceFrame
		int[] actualResult = StacktraceView.getAllocationData(stFrame);
		int[] expectedResult = {0, 0};
		Assert.assertEquals(actualResult[0], expectedResult[0]);
		Assert.assertEquals(actualResult[1], expectedResult[1]);
	}
}