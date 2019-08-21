package org.openjdk.jmc.flightrecorder.ui.views.stacktrace;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.openjdk.jmc.common.collection.SimpleArray;
import org.openjdk.jmc.common.item.IItem;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceFrame;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceModel.Branch;
import org.openjdk.jmc.flightrecorder.stacktrace.StacktraceModel.Fork;

public class AllocationPressureTest {

	@Test
	public void allocationPressureCalculationTest() {
		StacktraceFrameProxy stframe = new StacktraceFrameProxy();
		int[] actualResult = StacktraceView.getAllocationData(stframe.toStub());
		int[] expectedResult = {0, 0};
		assertEquals(actualResult[0], expectedResult[0]);
		assertEquals(actualResult[1], expectedResult[1]);
	}

	private class StacktraceFrameProxy implements InvocationHandler {

		public StacktraceFrame toStub() {
			return (StacktraceFrame) Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] { StacktraceFrame.class },
                    this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getItems")) {
				// return mocked SimpleArray<IItem>
			} else if (method.getName().equals("getBranch")) {
				// return mocked Branch
			} 
			return null;
		}
	}

	private class BranchProxy implements InvocationHandler {

		public Branch toStub() {
			return (Branch) Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] { Branch.class },
                    this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getParentFork")) {
				// return mocked Fork
			}
			return null;
		}
	}

	private class ForkProxy implements InvocationHandler {

		public Fork toStub() {
			return (Fork) Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] { Fork.class },
                    this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("getItemsArray")) {
				// return mocked SimpleArray<IItem>
			}
			return null;
		}
	}

	private class SimpleArrayIItemProxy implements InvocationHandler {

		public SimpleArray<IItem> toStub() {
			return (SimpleArray<IItem>) Proxy.newProxyInstance(
                    this.getClass().getClassLoader(),
                    new Class[] { SimpleArray.class },
                    this);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("iterator")) {
				// return an iterator
			} else if (method.getName().equals("size")) {
				// return int/Integer for size
			}
			return null;
		}
	}
}