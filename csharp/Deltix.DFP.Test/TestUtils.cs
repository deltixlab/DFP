using NUnit.Framework;
using System;
using System.Collections.Generic;
using System.Text;

namespace Deltix.DFP.Test
{
	public class TestUtils
	{
		public static void AssertDfp(UInt64 expected, UInt64 actual)
		{
			if (expected != actual)
				Assert.AreEqual(DotNetImpl.ToDebugString(expected), DotNetImpl.ToDebugString(actual));
		}

		public static void AssertDfp(Decimal64 expected, Decimal64 actual) => AssertDfp(expected.ToUnderlying(), actual.ToUnderlying());

		public static void AssertDfpEq(Decimal64 expected, Decimal64 actual)
		{
			if (!expected.IsEqual(actual))
				Assert.AreEqual(DotNetImpl.ToDebugString(expected.ToUnderlying()), DotNetImpl.ToDebugString(actual.ToUnderlying()));
		}
	}
}
