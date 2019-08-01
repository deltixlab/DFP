#define USE_MONO_WORKAROUND

using RTMath.Utilities;
using System;
using System.Runtime.InteropServices;

namespace Deltix.DFP
{
	internal static class NativeImpl

/*	Mono workaround costs 0-0.7 ns / call
	(when measured on Windows/.NET core/64, but probably within this range on all platforms)
*/
#if USE_MONO_WORKAROUND
	{
		private static readonly NativeImportsWrapper _impl;
		static NativeImpl()
		{
			_impl = new NativeImportsWrapper();
			_impl.VerifyVersion();
		}

	#region Conversion

		public static UInt64 fromFloat64(Double doubleValue) => _impl.fromFloat64(doubleValue);
		public static Double toFloat64(UInt64 x) => _impl.toFloat64(x);
		public static UInt64 fromFixedPoint64(Int64 mantissa, Int32 numberOfDigits) => _impl.fromFixedPoint64(mantissa, numberOfDigits);
		public static UInt64 fromFixedPoint32(Int32 mantissa, Int32 numberOfDigits) => _impl.fromFixedPoint32(mantissa, numberOfDigits);
		public static UInt64 fromFixedPointU32(UInt32 mantissa, Int32 numberOfDigits) => _impl.fromFixedPointU32(mantissa, numberOfDigits);
		public static Int64 toFixedPoint(UInt64 x, Int32 numberOfDigits) => _impl.toFixedPoint(x, numberOfDigits);
		public static UInt64 fromInt64(Int64 integer) => _impl.fromInt64(integer);
		public static Int64 toInt64(UInt64 x) => _impl.toInt64(x);
		public static UInt64 fromUInt64(UInt64 integer) => _impl.fromUInt64(integer);
		public static UInt64 toUInt64(UInt64 x) => _impl.toUInt64(x);
		public static Int32 toInt32(UInt64 x) => _impl.toInt32(x);
		public static UInt32 toUInt32(UInt64 x) => _impl.toUInt32(x);
		public static Int16 toInt16(UInt64 x) => _impl.toInt16(x);
		public static UInt16 toUInt16(UInt64 x) => _impl.toUInt16(x);
		public static SByte toInt8(UInt64 x) => _impl.toInt8(x);
		public static Byte toUInt8(UInt64 x) => _impl.toUInt8(x);

	#endregion

	#region Classification

		public static Boolean isNaN(UInt64 x) => _impl.isNaN(x);
		public static Boolean isInfinity(UInt64 x) => _impl.isInfinity(x);
		public static Boolean isPositiveInfinity(UInt64 x) => _impl.isPositiveInfinity(x);
		public static Boolean isNegativeInfinity(UInt64 x) => _impl.isNegativeInfinity(x);
		public static Boolean isFinite(UInt64 x) => _impl.isFinite(x);
		public static Boolean isNormal(UInt64 x) => _impl.isNormal(x);
		public static Boolean signBit(UInt64 x) => _impl.signBit(x);

	#endregion

	#region Comparison

		public static Int32 compare(UInt64 a, UInt64 b) => _impl.compare(a, b);
		public static Boolean isEqual(UInt64 a, UInt64 b) => _impl.isEqual(a, b);
		public static Boolean isNotEqual(UInt64 a, UInt64 b) => _impl.isNotEqual(a, b);
		public static Boolean isLess(UInt64 a, UInt64 b) => _impl.isLess(a, b);
		public static Boolean isLessOrEqual(UInt64 a, UInt64 b) => _impl.isLessOrEqual(a, b);
		public static Boolean isGreater(UInt64 a, UInt64 b) => _impl.isGreater(a, b);
		public static Boolean isGreaterOrEqual(UInt64 a, UInt64 b) => _impl.isGreaterOrEqual(a, b);
		public static Boolean isZero(UInt64 x) => _impl.isZero(x);
		public static Boolean isNonZero(UInt64 x) => _impl.isNonZero(x);
		public static Boolean isPositive(UInt64 x) => _impl.isPositive(x);
		public static Boolean isNegative(UInt64 x) => _impl.isNegative(x);
		public static Boolean isNonPositive(UInt64 x) => _impl.isNonPositive(x);
		public static Boolean isNonNegative(UInt64 x) => _impl.isNonNegative(x);

	#endregion

	#region Rounding

		public static UInt64 roundTowardsPositiveInfinity(UInt64 x) => _impl.roundTowardsPositiveInfinity(x);
		public static UInt64 roundTowardsNegativeInfinity(UInt64 x) => _impl.roundTowardsNegativeInfinity(x);
		public static UInt64 roundTowardsZero(UInt64 x) => _impl.roundTowardsZero(x);
		public static UInt64 roundToNearestTiesAwayFromZero(UInt64 x) => _impl.roundToNearestTiesAwayFromZero(x);

	#endregion

	#region Minimum & Maximum

		public static UInt64 max2(UInt64 a, UInt64 b) => _impl.max2(a, b);
		public static UInt64 max3(UInt64 a, UInt64 b, UInt64 c) => _impl.max3(a, b, c);
		public static UInt64 max4(UInt64 a, UInt64 b, UInt64 c, UInt64 d) => _impl.max4(a, b, c, d);
		public static UInt64 min2(UInt64 a, UInt64 b) => _impl.min2(a, b);
		public static UInt64 min3(UInt64 a, UInt64 b, UInt64 c) => _impl.min3(a, b, c);
		public static UInt64 min4(UInt64 a, UInt64 b, UInt64 c, UInt64 d) => _impl.min4(a, b, c, d);

	#endregion

	#region Arithmetic

		public static UInt64 negate(UInt64 x) => _impl.negate(x);
		public static UInt64 abs(UInt64 x) => _impl.abs(x);
		public static UInt64 add2(UInt64 a, UInt64 b) => _impl.add2(a, b);
		public static UInt64 add3(UInt64 a, UInt64 b, UInt64 c) => _impl.add3(a, b, c);
		public static UInt64 add4(UInt64 a, UInt64 b, UInt64 c, UInt64 d) => _impl.add4(a, b, c, d);
		public static UInt64 subtract(UInt64 a, UInt64 b) => _impl.subtract(a, b);
		public static UInt64 multiply2(UInt64 a, UInt64 b) => _impl.multiply2(a, b);
		public static UInt64 multiply3(UInt64 a, UInt64 b, UInt64 c) => _impl.multiply3(a, b, c);
		public static UInt64 multiply4(UInt64 a, UInt64 b, UInt64 c, UInt64 d) => _impl.multiply4(a, b, c, d);
		public static UInt64 multiplyByInt32(UInt64 x, Int32 integer) => _impl.multiplyByInt32(x, integer);
		public static UInt64 multiplyByInt64(UInt64 a, Int64 integer) => _impl.multiplyByInt64(a, integer);
		public static UInt64 divide(UInt64 a, UInt64 b) => _impl.divide(a, b);
		public static UInt64 divideByInt32(UInt64 x, Int32 integer) => _impl.divideByInt32(x, integer);
		public static UInt64 divideByInt64(UInt64 x, Int64 integer) => _impl.divideByInt64(x, integer);
		public static UInt64 multiplyAndAdd(UInt64 a, UInt64 b, UInt64 c) => _impl.multiplyAndAdd(a, b, c);
		public static UInt64 scaleByPowerOfTen(UInt64 x, Int32 n) => _impl.scaleByPowerOfTen(x, n);
		public static UInt64 mean2(UInt64 a, UInt64 b) => _impl.mean2(a, b);

	#endregion

	#region Special

		public static UInt64 nextUp(UInt64 x) => _impl.nextUp(x);
		public static UInt64 nextDown(UInt64 x) => _impl.nextDown(x);

	#endregion
	}

	internal sealed class NativeImportsWrapper
	{
		static NativeImportsWrapper()
		{
			NativeImports.Init();
		}

		internal void VerifyVersion()
		{
			NativeImports.VerifyVersion();
		}

	#region Conversion

		internal UInt64 fromFloat64(Double doubleValue) => NativeImports.fromFloat64(doubleValue);
		internal Double toFloat64(UInt64 x) => NativeImports.toFloat64(x);
		internal UInt64 fromFixedPoint64(Int64 mantissa, Int32 numberOfDigits) => NativeImports.fromFixedPoint64(mantissa, numberOfDigits);
		internal UInt64 fromFixedPoint32(Int32 mantissa, Int32 numberOfDigits)=> NativeImports.fromFixedPoint32(mantissa, numberOfDigits);
		internal UInt64 fromFixedPointU32(UInt32 mantissa, Int32 numberOfDigits)=> NativeImports.fromFixedPointU32(mantissa, numberOfDigits);
		internal Int64 toFixedPoint(UInt64 x, Int32 numberOfDigits)=> NativeImports.toFixedPoint(x, numberOfDigits);
		internal UInt64 fromInt64(Int64 integer)=> NativeImports.fromInt64(integer);
		internal Int64 toInt64(UInt64 x)=> NativeImports.toInt64(x);
		internal UInt64 fromUInt64(UInt64 integer)=> NativeImports.fromUInt64(integer);
		internal UInt64 toUInt64(UInt64 x)=> NativeImports.toUInt64(x);
		internal Int32 toInt32(UInt64 x)=> NativeImports.toInt32(x);
		internal UInt32 toUInt32(UInt64 x)=> NativeImports.toUInt32(x);
		internal Int16 toInt16(UInt64 x)=> NativeImports.toInt16(x);
		internal UInt16 toUInt16(UInt64 x)=> NativeImports.toUInt16(x);
		internal SByte toInt8(UInt64 x)=> NativeImports.toInt8(x);
		internal Byte toUInt8(UInt64 x)=> NativeImports.toUInt8(x);

	#endregion

	#region Classification

		internal Boolean isNaN(UInt64 x)=> NativeImports.isNaN(x);
		internal Boolean isInfinity(UInt64 x)=> NativeImports.isInfinity(x);
		internal Boolean isPositiveInfinity(UInt64 x)=> NativeImports.isPositiveInfinity(x);
		internal Boolean isNegativeInfinity(UInt64 x)=> NativeImports.isNegativeInfinity(x);
		internal Boolean isFinite(UInt64 x)=> NativeImports.isFinite(x);
		internal Boolean isNormal(UInt64 x)=> NativeImports.isNormal(x);
		internal Boolean signBit(UInt64 x)=> NativeImports.signBit(x);

	#endregion

	#region Comparison

		internal Int32 compare(UInt64 a, UInt64 b)=> NativeImports.compare(a, b);
		internal Boolean isEqual(UInt64 a, UInt64 b)=> NativeImports.isEqual(a, b);
		internal Boolean isNotEqual(UInt64 a, UInt64 b)=> NativeImports.isNotEqual(a, b);
		internal Boolean isLess(UInt64 a, UInt64 b)=> NativeImports.isLess(a, b);
		internal Boolean isLessOrEqual(UInt64 a, UInt64 b)=> NativeImports.isLessOrEqual(a, b);
		internal Boolean isGreater(UInt64 a, UInt64 b)=> NativeImports.isGreater(a, b);
		internal Boolean isGreaterOrEqual(UInt64 a, UInt64 b)=> NativeImports.isGreaterOrEqual(a, b);
		internal Boolean isZero(UInt64 x)=> NativeImports.isZero(x);
		internal Boolean isNonZero(UInt64 x)=> NativeImports.isNonZero(x);
		internal Boolean isPositive(UInt64 x)=> NativeImports.isPositive(x);
		internal Boolean isNegative(UInt64 x)=> NativeImports.isNegative(x);
		internal Boolean isNonPositive(UInt64 x)=> NativeImports.isNonPositive(x);
		internal Boolean isNonNegative(UInt64 x)=> NativeImports.isNonNegative(x);

	#endregion

	#region Rounding

		internal UInt64 roundTowardsPositiveInfinity(UInt64 x)=> NativeImports.roundTowardsPositiveInfinity(x);
		internal UInt64 roundTowardsNegativeInfinity(UInt64 x)=> NativeImports.roundTowardsNegativeInfinity(x);
		internal UInt64 roundTowardsZero(UInt64 x)=> NativeImports.roundTowardsZero(x);
		internal UInt64 roundToNearestTiesAwayFromZero(UInt64 x)=> NativeImports.roundToNearestTiesAwayFromZero(x);

	#endregion

	#region Minimum & Maximum

		internal UInt64 max2(UInt64 a, UInt64 b)=> NativeImports.max2(a, b);
		internal UInt64 max3(UInt64 a, UInt64 b, UInt64 c)=> NativeImports.max3(a, b, c);
		internal UInt64 max4(UInt64 a, UInt64 b, UInt64 c, UInt64 d)=> NativeImports.max4(a, b, c, d);
		internal UInt64 min2(UInt64 a, UInt64 b)=> NativeImports.min2(a, b);
		internal UInt64 min3(UInt64 a, UInt64 b, UInt64 c)=> NativeImports.min3(a, b, c);
		internal UInt64 min4(UInt64 a, UInt64 b, UInt64 c, UInt64 d)=> NativeImports.min4(a, b, c, d);

	#endregion

	#region Arithmetic

		internal UInt64 negate(UInt64 x)=> NativeImports.negate(x);
		internal UInt64 abs(UInt64 x)=> NativeImports.abs(x);
		internal UInt64 add2(UInt64 a, UInt64 b)=> NativeImports.add2(a, b);
		internal UInt64 add3(UInt64 a, UInt64 b, UInt64 c)=> NativeImports.add3(a, b, c);
		internal UInt64 add4(UInt64 a, UInt64 b, UInt64 c, UInt64 d)=> NativeImports.add4(a, b, c, d);
		internal UInt64 subtract(UInt64 a, UInt64 b)=> NativeImports.subtract(a, b);
		internal UInt64 multiply2(UInt64 a, UInt64 b)=> NativeImports.multiply2(a, b);
		internal UInt64 multiply3(UInt64 a, UInt64 b, UInt64 c)=> NativeImports.multiply3(a, b, c);
		internal UInt64 multiply4(UInt64 a, UInt64 b, UInt64 c, UInt64 d)=> NativeImports.multiply4(a, b, c, d);
		internal UInt64 multiplyByInt32(UInt64 x, Int32 integer)=> NativeImports.multiplyByInt32(x, integer);
		internal UInt64 multiplyByInt64(UInt64 a, Int64 integer)=> NativeImports.multiplyByInt64(a, integer);
		internal UInt64 divide(UInt64 a, UInt64 b)=> NativeImports.divide(a, b);
		internal UInt64 divideByInt32(UInt64 x, Int32 integer)=> NativeImports.divideByInt32(x, integer);
		internal UInt64 divideByInt64(UInt64 x, Int64 integer)=> NativeImports.divideByInt64(x, integer);
		internal UInt64 multiplyAndAdd(UInt64 a, UInt64 b, UInt64 c)=> NativeImports.multiplyAndAdd(a, b, c);
		internal UInt64 scaleByPowerOfTen(UInt64 x, Int32 n)=> NativeImports.scaleByPowerOfTen(x, n);
		internal UInt64 mean2(UInt64 a, UInt64 b)=> NativeImports.mean2(a, b);

	#endregion

	#region Special

		internal UInt64 nextUp(UInt64 x)=> NativeImports.nextUp(x);
		internal UInt64 nextDown(UInt64 x)=> NativeImports.nextDown(x);

	#endregion
	}

	internal class NativeImports
#endif // #if USE_MONO_WORKAROUND
	{
		internal const string NativeApiVersion = "2";
		internal const string DllName = "DecimalNative";
		private const string Dll = DllName + NativeApiVersion;

		static internal void Init()
		{
			ResourceLoader
				.From("Deltix.DFP.$(OS)._$(ARCH).*")
				.To(".rtmath/dfp/$(VERSION)/$(ARCH)")
				.AddDllSuffix(NativeApiVersion)
				.TryRandomFallbackSubDirectory(true)
				.Load();
		}

		static internal void VerifyVersion()
		{
			int ver = 0;
			try
			{
				ver = version();
				if (Int32.Parse(NativeApiVersion) == ver)
					return;

			} catch (EntryPointNotFoundException)
			{
				ver = -1;
			}

			throw new TypeLoadException(String.Format("Native library <{0}> API version mismatch V{1} expected, V{2} found", Dll, NativeApiVersion, ver));
		}

#if !USE_MONO_WORKAROUND
		static NativeImpl()
		{
			Init();
			VerifyVersion();
		}
#endif // #if USE_MONO_WORKAROUND

		[DllImport(Dll)]
		public static extern Int32 version();

#region Conversion

		[DllImport(Dll)]
		public static extern UInt64 fromFloat64(Double doubleValue);

		[DllImport(Dll)]
		public static extern Double toFloat64(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt64 fromFixedPoint64(Int64 mantissa, Int32 numberOfDigits);

		[DllImport(Dll)]
		public static extern UInt64 fromFixedPoint32(Int32 mantissa, Int32 numberOfDigits);

		[DllImport(Dll)]
		public static extern UInt64 fromFixedPointU32(UInt32 mantissa, Int32 numberOfDigits);

		[DllImport(Dll)]
		public static extern Int64 toFixedPoint(UInt64 x, Int32 numberOfDigits);

		[DllImport(Dll)]
		public static extern UInt64 fromInt64(Int64 integer);

		[DllImport(Dll)]
		public static extern Int64 toInt64(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt64 fromUInt64(UInt64 integer);

		[DllImport(Dll)]
		public static extern UInt64 toUInt64(UInt64 x);

		//[DllImport(Dll)]
		//public static extern UInt64 fromInt32(Int32 value);

		[DllImport(Dll)]
		public static extern Int32 toInt32(UInt64 x);

		//[DllImport(Dll)]
		//public static extern UInt64 fromUInt32(UInt32 value);

		[DllImport(Dll)]
		public static extern UInt32 toUInt32(UInt64 x);

		[DllImport(Dll)]
		public static extern Int16 toInt16(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt16 toUInt16(UInt64 x);

		[DllImport(Dll)]
		public static extern SByte toInt8(UInt64 x);

		[DllImport(Dll)]
		public static extern Byte toUInt8(UInt64 x);

#endregion

#region Classification

		[DllImport(Dll)]
		public static extern Boolean isNaN(UInt64 x);

		[DllImport(Dll)]
		public static extern Boolean isInfinity(UInt64 x);

		[DllImport(Dll)]
		public static extern Boolean isPositiveInfinity(UInt64 x);

		[DllImport(Dll)]
		public static extern Boolean isNegativeInfinity(UInt64 x);

		[DllImport(Dll)]
		public static extern Boolean isFinite(UInt64 x);

		[DllImport(Dll)]
		public static extern Boolean isNormal(UInt64 x);

		[DllImport(Dll)]
		public static extern Boolean signBit(UInt64 x);

#endregion

#region Comparison

		[DllImport(Dll)]
		public static extern Int32 compare(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isEqual(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isNotEqual(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isLess(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isLessOrEqual(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isGreater(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isGreaterOrEqual(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern Boolean isZero(UInt64 a);

		[DllImport(Dll)]
		public static extern Boolean isNonZero(UInt64 a);

		[DllImport(Dll)]
		public static extern Boolean isPositive(UInt64 a);

		[DllImport(Dll)]
		public static extern Boolean isNegative(UInt64 a);

		[DllImport(Dll)]
		public static extern Boolean isNonPositive(UInt64 a);

		[DllImport(Dll)]
		public static extern Boolean isNonNegative(UInt64 b);

#endregion

#region Rounding

		[DllImport(Dll)]
		public static extern UInt64 roundTowardsPositiveInfinity(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt64 roundTowardsNegativeInfinity(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt64 roundTowardsZero(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt64 roundToNearestTiesAwayFromZero(UInt64 x);

#endregion

#region Minimum & Maximum

		[DllImport(Dll)]
		public static extern UInt64 max2(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern UInt64 max3(UInt64 a, UInt64 b, UInt64 c);

		[DllImport(Dll)]
		public static extern UInt64 max4(UInt64 a, UInt64 b, UInt64 c, UInt64 d);

		[DllImport(Dll)]
		public static extern UInt64 min2(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern UInt64 min3(UInt64 a, UInt64 b, UInt64 c);

		[DllImport(Dll)]
		public static extern UInt64 min4(UInt64 a, UInt64 b, UInt64 c, UInt64 d);

#endregion

#region Arithmetic

		[DllImport(Dll)]
		public static extern UInt64 negate(UInt64 a);

		[DllImport(Dll)]
		public static extern UInt64 abs(UInt64 a);

		[DllImport(Dll)]
		public static extern UInt64 add2(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern UInt64 add3(UInt64 a, UInt64 b, UInt64 c);

		[DllImport(Dll)]
		public static extern UInt64 add4(UInt64 a, UInt64 b, UInt64 c, UInt64 d);

		[DllImport(Dll)]
		public static extern UInt64 subtract(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern UInt64 multiply2(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern UInt64 multiply3(UInt64 a, UInt64 b, UInt64 c);

		[DllImport(Dll)]
		public static extern UInt64 multiply4(UInt64 a, UInt64 b, UInt64 c, UInt64 d);

		[DllImport(Dll)]
		public static extern UInt64 multiplyByInt32(UInt64 a, Int32 integer);

		[DllImport(Dll)]
		public static extern UInt64 multiplyByInt64(UInt64 a, Int64 integer);

		[DllImport(Dll)]
		public static extern UInt64 divide(UInt64 a, UInt64 b);

		[DllImport(Dll)]
		public static extern UInt64 divideByInt32(UInt64 a, Int32 b);

		[DllImport(Dll)]
		public static extern UInt64 divideByInt64(UInt64 a, Int64 b);

		[DllImport(Dll)]
		public static extern UInt64 multiplyAndAdd(UInt64 a, UInt64 b, UInt64 c);

		[DllImport(Dll)]
		public static extern UInt64 scaleByPowerOfTen(UInt64 a, Int32 n);

		[DllImport(Dll)]
		public static extern UInt64 mean2(UInt64 a, UInt64 b);

#endregion

#region Special

		[DllImport(Dll)]
		public static extern UInt64 nextUp(UInt64 x);

		[DllImport(Dll)]
		public static extern UInt64 nextDown(UInt64 x);

#endregion
	}
}
