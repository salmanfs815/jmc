package org.openjdk.jmc.flightrecorder.ui.views.stacktrace;

import static org.mockito.Mockito.*;

import org.junit.Assert;
import org.junit.Test;
import org.openjdk.jmc.common.collection.SimpleArray;
import org.openjdk.jmc.common.item.IItem;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceFrame;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceModel.Branch;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceModel.Fork;

public class AllocationPressureTest {

	@Test
	public void allocationPressureCalculationTest() {

		IItem item1 = mock(IItem.class);
		IItem item2 = mock(IItem.class);
		IItem item3 = mock(IItem.class);
		IItem item4 = mock(IItem.class);
		when(item1.toString()).thenReturn("Type(jdk.ObjectAllocationOutsideTLAB) 2019-08-12, 12:12:46 p.m. RMI TCP Connection(6)-10.15.17.142 org.openjdk.jmc.flightrecorder.internal.parser.v1.StructTypes$JfrStackTrace@b7bdf424 int[] 720 B");
		when(item2.toString()).thenReturn("Type(jdk.ObjectAllocationOutsideTLAB) 2019-08-12, 12:12:46 p.m. RMI TCP Connection(6)-10.15.17.142 org.openjdk.jmc.flightrecorder.internal.parser.v1.StructTypes$JfrStackTrace@df979b58 java.lang.Object[] 368 B");
		when(item3.toString()).thenReturn("Type(jdk.ObjectAllocationOutsideTLAB) 2019-08-12, 12:12:46 p.m. RMI TCP Connection(6)-10.15.17.142 org.openjdk.jmc.flightrecorder.internal.parser.v1.StructTypes$JfrStackTrace@69ef8a31 java.lang.Object[] 720 B");
		when(item4.toString()).thenReturn("Type(jdk.ObjectAllocationOutsideTLAB) 2019-08-12, 12:12:47 p.m. RMI TCP Connection(6)-10.15.17.142 org.openjdk.jmc.flightrecorder.internal.parser.v1.StructTypes$JfrStackTrace@f35b6b64 int[] 11 KiB");
		IItem[] itemArray1 = {item1, item2};
		IItem[] itemArray2 = {item3, item4};
		SimpleArray<IItem> simpleArray1 = new SimpleArray<IItem>(itemArray1);
		SimpleArray<IItem> simpleArray2 = new SimpleArray<IItem>(itemArray2);

		StacktraceFrame frame = mock(StacktraceFrame.class);
		Branch branch = mock(Branch.class);
		Fork fork = mock(Fork.class);
		when(frame.getItems()).thenReturn(simpleArray1);
		when(frame.getBranch()).thenReturn(branch);
		when(branch.getParentFork()).thenReturn(fork);
		when(fork.getItemsArray()).thenReturn(simpleArray2);

		int[] actualResult = StacktraceView.getAllocationData(frame);
		int[] expectedResult = {0, 0};
		Assert.assertEquals(actualResult[0], expectedResult[0]);
		Assert.assertEquals(actualResult[1], expectedResult[1]);
	}
}