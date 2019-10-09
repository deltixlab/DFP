using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;
using NUnit.Framework;

namespace Deltix.DFP.Test
{
	[TestFixture]
	class Decimal64Test
	{
		[TestCase()]
		public void IsNullTest()
		{
			Decimal64 a = Decimal64.Null;
			Assert.AreEqual(true, a.IsNull());
			Assert.AreEqual(true, a.IsNaN());
			Decimal64 b = Decimal64.NaN;
			Assert.AreEqual(false, b.IsNull());
			Assert.AreEqual(true, b.IsNaN());
		}

		[TestCase()]
		public void CanonizeTest()
		{
			Decimal64 a = Decimal64.FromFixedPoint(10000, 4);
			Decimal64 b = Decimal64.FromFixedPoint(1, 0);
			Decimal64 c = Decimal64.FromFixedPoint(10, 1);
			Decimal64 d = Decimal64.FromFixedPoint(100, 2);
			Decimal64 e = Decimal64.FromFixedPoint(1000, 3);

			Assert.AreEqual(true, a.Equals(b));
			Assert.AreEqual(true, b.Equals(c));
			Assert.AreEqual(true, c.Equals(d));
			Assert.AreEqual(true, d.Equals(e));
			Assert.AreEqual(true, e.Equals(a));

			Assert.AreEqual(a.GetHashCode(), b.GetHashCode());
			Assert.AreEqual(b.GetHashCode(), c.GetHashCode());
			Assert.AreEqual(c.GetHashCode(), d.GetHashCode());
			Assert.AreEqual(d.GetHashCode(), e.GetHashCode());
			Assert.AreEqual(e.GetHashCode(), a.GetHashCode());

			Decimal64 nan1 = Decimal64.NaN;
			Decimal64 nan2 = Decimal64.FromUnderlying(nan1.Bits + 20);

			Assert.AreEqual(true, nan1.Equals(nan2));
			Assert.AreEqual(nan1.GetHashCode(), nan2.GetHashCode());


			Decimal64 posInf1 = Decimal64.PositiveInfinity;
			Decimal64 posInf2 = Decimal64.FromUnderlying(posInf1.Bits + 10);

			Assert.AreEqual(true, posInf1.Equals(posInf2));
			Assert.AreEqual(posInf1.GetHashCode(), posInf2.GetHashCode());

			Decimal64 negInf1 = Decimal64.PositiveInfinity;
			Decimal64 negInf2 = Decimal64.FromUnderlying(negInf1.Bits + 10);

			Assert.AreEqual(true, negInf1.Equals(negInf2));
			Assert.AreEqual(negInf1.GetHashCode(), negInf2.GetHashCode());

			Decimal64 zero1 = Decimal64.FromFixedPoint(0, 1);
			Decimal64 zero2 = Decimal64.FromFixedPoint(0, 2);

			Assert.AreEqual(true, zero1.Equals(zero2));
			Assert.AreEqual(zero1.GetHashCode(), zero2.GetHashCode());

		}


		[Test]
		public void Binary64Conversion()
		{
			Double binary64 = new Random().NextDouble();
			Decimal64 decimal64 = Decimal64.FromDouble(binary64);
			Double result = (Double)decimal64;
			Assert.That(Math.Abs(binary64 - result), Is.LessThanOrEqualTo(1.0E-16));
		}

		[Test]
		public void FromFixedPointFastConsts()
		{
			TestUtils.AssertDfp(Decimal64.Zero.Bits, DotNetImpl.FromFixedPointFast(0, 0));
			AssertDfp(Decimal64.One, DotNetImpl.FromFixedPointFast(1, 0));
			AssertDfp(Decimal64.Two, DotNetImpl.FromFixedPointFast(2, 0));
			AssertDfp(Decimal64.Ten, DotNetImpl.FromFixedPointFast(10, 0));
			AssertDfpEq(Decimal64.Ten, DotNetImpl.FromFixedPointFast(1, -1));
			AssertDfp(Decimal64.Hundred, DotNetImpl.FromFixedPointFast(100, 0));
			AssertDfpEq(Decimal64.Hundred, DotNetImpl.FromFixedPointFast(1, -2));
			AssertDfpEq(Decimal64.Thousand, DotNetImpl.FromFixedPointFast(1, -3));
			AssertDfpEq(Decimal64.Million, DotNetImpl.FromFixedPointFast(1, -6));
			AssertDfp(Decimal64.OneTenth, DotNetImpl.FromFixedPointFast(1, 1));
			AssertDfp(Decimal64.OneHundredth, DotNetImpl.FromFixedPointFast(1, 2));

			AssertDfp(Decimal64.Zero, DotNetImpl.FromFixedPointFastUnsigned(0, 0));
			AssertDfp(Decimal64.One, DotNetImpl.FromFixedPointFastUnsigned(1, 0));
			AssertDfp(Decimal64.Two, DotNetImpl.FromFixedPointFastUnsigned(2, 0));
			AssertDfp(Decimal64.Ten, DotNetImpl.FromFixedPointFastUnsigned(10, 0));
			AssertDfpEq(Decimal64.Ten, DotNetImpl.FromFixedPointFastUnsigned(1, -1));
			AssertDfp(Decimal64.Hundred, DotNetImpl.FromFixedPointFastUnsigned(100, 0));
			AssertDfpEq(Decimal64.Hundred, DotNetImpl.FromFixedPointFastUnsigned(1, -2));
			AssertDfpEq(Decimal64.Thousand, DotNetImpl.FromFixedPointFastUnsigned(1, -3));
			AssertDfpEq(Decimal64.Million, DotNetImpl.FromFixedPointFastUnsigned(1, -6));
		}

		private void AssertDfp(UInt64 actual, UInt64 expected) => TestUtils.AssertDfp(actual, expected);
		private void AssertDfp(UInt64 actual, Decimal64 expected) => TestUtils.AssertDfp(actual, expected.Bits);
		private void AssertDfp(Decimal64 actual, Decimal64 expected) => TestUtils.AssertDfp(actual, expected);
		private void AssertDfp(Decimal64 actual, UInt64 expected) => TestUtils.AssertDfp(actual.Bits, expected);
		private void AssertDfpEq(Decimal64 actual, Decimal64 expected) => TestUtils.AssertDfp(actual, expected);
		private void AssertDfpEq(Decimal64 actual, UInt64 expected) => TestUtils.AssertDfpEq(actual, Decimal64.FromUnderlying(expected));
		

		[Test]
		public void FromFixedPointFast()
		{
			int N = 10000;
			Random random = new Random();
			for (int exp = 398 - 0x2FF; exp <= 398; ++exp)
			{
				for (int j = 0; j < N; ++j)
				{
					int mantissa = random.Next();
					long mantissa64 = ((long)mantissa << 32) + random.Next();
					if (random.Next() < 0)
						mantissa64 = -mantissa64;

					UInt64 correct32 = NativeImpl.fromFixedPoint32(mantissa, exp);
					UInt64 correctU32 = NativeImpl.fromFixedPointU32((UInt32)mantissa, exp);
					UInt64 correct64 = NativeImpl.fromFixedPoint64(mantissa, exp);

					AssertDfp(correct32, DotNetImpl.FromFixedPointFast(mantissa, exp));
					AssertDfp(correct64, DotNetImpl.FromFixedPointFast(mantissa, exp));
					AssertDfp(correct64, DotNetImpl.FromFixedPointFast(mantissa, exp));

					AssertDfp(correctU32, DotNetImpl.FromFixedPointFastUnsigned((UInt32)mantissa, exp));
					AssertDfp(NativeImpl.fromFixedPoint64((UInt32)mantissa, exp), DotNetImpl.FromFixedPointFastUnsigned((UInt32)mantissa, exp));

					AssertDfp(correct32, Decimal64.FromFixedPoint(mantissa, exp));
					AssertDfp(correct64, Decimal64.FromFixedPoint(mantissa, exp));
					AssertDfp(correctU32, Decimal64.FromFixedPoint((UInt32)mantissa, exp));
					AssertDfp(NativeImpl.fromFixedPoint64((UInt32)mantissa, exp), Decimal64.FromFixedPoint((UInt32)mantissa, exp));
					AssertDfp(correct32, Decimal64.FromFixedPoint((Int64)mantissa, exp));
					AssertDfp(correct64, Decimal64.FromFixedPoint((Int64)mantissa, exp));

					if (mantissa >= 0)
					{
						AssertDfp(correct32, DotNetImpl.FromFixedPointFastUnsigned((UInt32)mantissa, exp));
						AssertDfp(correct64, DotNetImpl.FromFixedPointFastUnsigned((UInt32)mantissa, exp));
					}

					for (int k = 0; k < 32; ++k)
					{
						AssertDfp(NativeImpl.fromFixedPoint64(mantissa, exp), Decimal64.FromFixedPoint(mantissa, exp));
						AssertDfp(NativeImpl.fromFixedPoint64(mantissa64, exp), Decimal64.FromFixedPoint(mantissa64, exp));
						mantissa >>= 1;
						mantissa64 >>= 1;
					}
				}

				AssertDfp(NativeImpl.fromFixedPoint32(0, exp), DotNetImpl.FromFixedPointFast(0, exp));
				AssertDfp(NativeImpl.fromFixedPoint32(Int32.MinValue, exp), DotNetImpl.FromFixedPointFast(Int32.MinValue, exp));
				AssertDfp(NativeImpl.fromFixedPoint32(Int32.MaxValue, exp), DotNetImpl.FromFixedPointFast(Int32.MaxValue, exp));
				AssertDfp(NativeImpl.fromFixedPoint64(0, exp), DotNetImpl.FromFixedPointFast(0, exp));
				AssertDfp(NativeImpl.fromFixedPoint64(Int32.MinValue, exp), DotNetImpl.FromFixedPointFast(Int32.MinValue, exp));
				AssertDfp(NativeImpl.fromFixedPoint64(Int32.MaxValue, exp), DotNetImpl.FromFixedPointFast(Int32.MaxValue, exp));
			}
		}

		[Test]
		public void FromFixedPointFastMin()
		{
			Assert.Throws<ArgumentException>(delegate { DotNetImpl.FromFixedPointFast(0, 398 - 0x300); });
		}

		[Test]
		public void FromFixedPointFastMax()
		{
			Assert.Throws<ArgumentException>(delegate { DotNetImpl.FromFixedPointFast(0, 399); });
		}

		[Test]
		public void FromFixedPointFastUMin()
		{
			Assert.Throws<ArgumentException>(delegate { DotNetImpl.FromFixedPointFastUnsigned(0, 398 - 0x300); });
		}

		[Test]
		public void FromFixedPointFastUMax()
		{
			Assert.Throws<ArgumentException>(delegate { DotNetImpl.FromFixedPointFastUnsigned(0, 399); });
		}


		private String PrintFp(Object a) => a.ToString().Replace(',', '.');

		private void AssertDecimalEqual(Object a, Object b) => Assert.AreEqual(PrintFp(a), PrintFp(b));
		private void AssertDecimalNotEqual(Object a, Object b) => Assert.AreNotEqual(PrintFp(a), PrintFp(b));

		[Test]
		public void DecimalInternalRepresentation()
		{
			Random random = new Random();
			Assert.AreEqual(16, sizeof(Decimal));
			for (int i = 0; i < 1000; ++i)
			{
				int lo = random.Next(), mid = random.Next(), hi = random.Next();
				byte exp = (byte)random.Next(0, 28);
				bool sign = random.Next() < 0;
				var x = new Decimal(lo, mid, hi, sign, exp);
				int lo2, mid2, hi2;
				byte exp2;
				bool sign2;
				UInt64 lo64;
				unsafe
				{
					int * f = (int *)&x; // Order (Little endian): flags, hi, lo, mid
					lo64 = ((ulong*) &x)[1];
					lo2 = f[2];
					mid2 = f[3];
					hi2 = f[1];
					short flg16 = ((short*) &x)[1];
					sign2 = flg16 < 0;
					//exp2 = (byte) (f[0] >> 16);
					exp2 = (byte)flg16;
				}

				Assert.AreEqual(lo, lo2);
				Assert.AreEqual(mid, mid2);
				Assert.AreEqual((uint)lo | ((ulong)(uint)mid << 32), lo64);
				Assert.AreEqual(lo, lo2);
				Assert.AreEqual(hi, hi2);
				Assert.AreEqual(sign, sign2);
				Assert.AreEqual(exp, exp2);
			}
		}


		[Test]
		public void DecimalConversionBasicFrom()
		{
			Decimal x = new Decimal(6) / new Decimal(10000);
			var bits = Decimal.GetBits(x);


			AssertDecimalEqual(x, (Decimal64)x);
			AssertDecimalEqual(x, (Decimal64)x);
			AssertDecimalEqual(x, Decimal64.FromDecimal(x));
			AssertDecimalEqual(0.0006M, (Decimal64)0.0006M);
			AssertDecimalEqual(0.0000006M, (Decimal64)0.0000006M);
			AssertDecimalEqual(0.00000000000006M, (Decimal64)0.00000000000006M);
			AssertDecimalEqual(9.8765432198721M, (Decimal64)9.8765432198721M);
			AssertDecimalEqual(0.00000098765432198721M, (Decimal64)0.00000098765432198721M);
			AssertDecimalEqual(0.0000000000000098765432198721M, (Decimal64)0.0000000000000098765432198721M);
			AssertDecimalEqual(1234567890123456M, (Decimal64)1234567890123456M);
			AssertDecimalEqual(1234567890123456000M, (Decimal64)1234567890123456000M);
			AssertDecimalEqual(0.00000000001M, (Decimal64)0.00000000001M);
			AssertDecimalNotEqual(1234567890123456111111M, (Decimal64)123456789012345111111M);

			AssertDecimalNotEqual(0.0006M, (Decimal64)(double)0.0006M);
			AssertDecimalNotEqual(0.0000000000000098765432198721M, (Decimal64)(double)0.0000000000000098765432198721M);
			AssertDecimalNotEqual(1234567890123456111111M, (Decimal64)(double)123456789012345111111M);
		}

		[Test]
		public void DecimalConversionTo()
		{
			Decimal x = new Decimal(6) / new Decimal(10000);
			Assert.AreEqual(x, (Decimal)Decimal64.FromFixedPoint(6, 4));
			AssertDecimalEqual(1234567890123456M, (Decimal)Decimal64.FromLong(1234567890123456));
			AssertDecimalEqual(0.1234567890123456M, (Decimal)Decimal64.FromFixedPoint(1234567890123456, 16));
			AssertDecimalEqual(0.00000000001234567890123456M, (Decimal)Decimal64.FromFixedPoint(1234567890123456, 26));

			AssertDecimalEqual(123456789012345.6M, (Decimal)Decimal64.FromFixedPoint(1234567890123456, 1));
			Assert.AreEqual(123456789012345.600000000M, (Decimal)(Decimal64.FromFixedPoint(1234567890123456, 1)));
			Assert.AreEqual(123456789012300.000000000M, (Decimal)(Decimal64.FromFixedPoint(1234567890123000, 1)));
			Assert.AreEqual(123456789012300.000000000M, (Decimal)(Decimal64.FromFixedPoint(1234567890123, -2)));

			// These are not converted precisely(TODO:)
			AssertDecimalNotEqual(12345678901234560M, (Decimal)Decimal64.FromLong(12345678901234560));
			AssertDecimalNotEqual(1234567890123456000M, (Decimal)Decimal64.FromLong(1234567890123456000));
			AssertDecimalNotEqual(123456789012345600000000M, (Decimal)Decimal64.FromFixedPoint(1234567890123456, -8));
			AssertDecimalNotEqual(7345678901234560001M, (Decimal)Decimal64.FromLong(7345678901234560001));
		}

		[Test]
		public void DecimalConversion2()
		{
			Decimal x = new Decimal(6) / new Decimal(10000);
			Assert.AreEqual(x, (Decimal)(Decimal64)x);
		}
		

		[Test]
		public void NumberConversionTest()
		{
			for (long i0 = 0; i0 < 9999999999999999L; i0 = (i0 << 1) + 1)
			{
				long x = i0;
				for (int i = 0; i < 2; i++, x = -x)
				{
					Assert.That(x, Is.EqualTo((long)Decimal64.FromDouble(x).ToDouble()));
					Assert.That(x, Is.EqualTo((long)Decimal64.FromDouble(x).ToLong()));
					Assert.True(Decimal64.FromDouble(x).Equals(Decimal64.FromLong(x)));
					Assert.True(Decimal64.FromLong(x).Equals(Decimal64.FromDouble(x)));
					Assert.That(x, Is.EqualTo((long)Decimal64.FromDouble(x)));

					if (x >= 0)
					{
						Assert.That(x, Is.EqualTo((long)(UInt64)Decimal64.FromDouble(x)));
					}
					else
					{
						Assert.True(1UL << 31 == (UInt32)Decimal64.FromLong(x));
						Assert.True(1UL << 63 == (UInt64)Decimal64.FromLong(x));
					}

					if (Math.Abs(x) <= Int32.MaxValue)
					{
						Assert.That(x, Is.EqualTo((long)Decimal64.FromLong(x).ToInt()));
						Assert.That(x, Is.EqualTo((long)Decimal64.FromInt((int)x).ToDouble()));
						Assert.That(x, Is.EqualTo((long)Decimal64.FromInt((int)x).ToLong()));
						Assert.That(x, Is.EqualTo((long)Decimal64.FromInt((int)x).ToInt()));
						Assert.True(Decimal64.FromDouble(x).Equals(Decimal64.FromInt((int)x)));
						Assert.True(Decimal64.FromLong(x).Equals(Decimal64.FromInt((int)x)));

						Assert.That(x, Is.EqualTo((int)Decimal64.FromDouble(x)));

						if (x >= 0)
						{
							Assert.That(x, Is.EqualTo((UInt32)Decimal64.FromDouble(x)));
						}
						else
						{
							Assert.That(1UL << 31, Is.EqualTo((UInt32)Decimal64.FromDouble(x)));
						}
					}
					else
					{
						// Expect integer overflow at some stage
						Assert.That(x, Is.Not.EqualTo((long)Decimal64.FromLong(x).ToInt()));
						Assert.That(x, Is.Not.EqualTo((long)Decimal64.FromInt((int)x).ToDouble()));
						Assert.That(x, Is.Not.EqualTo((long)Decimal64.FromInt((int)x).ToLong()));
						Assert.That(x, Is.Not.EqualTo((long)Decimal64.FromInt((int)x).ToInt()));
						Assert.False(Decimal64.FromDouble(x).Equals(Decimal64.FromInt((int)x)));
						Assert.False(Decimal64.FromLong(x).Equals(Decimal64.FromInt((int)x)));
					}
				}
			}
		}

		[Test]
		public void Min()
		{
			Assert.That(Decimal64.NaN.Min(Decimal64.Zero).Bits, Is.EqualTo(Decimal64.NaN.Bits));
			Assert.That(Decimal64.Zero.Min(Decimal64.NaN).Bits, Is.EqualTo(Decimal64.NaN.Bits));
			Assert.That(Decimal64.NaN.Min(Decimal64.NaN).Bits, Is.EqualTo(Decimal64.NaN.Bits));
		}

		[Test]
		public void Max()
		{
			Assert.That(Decimal64.NaN.Max(Decimal64.Zero).Bits, Is.EqualTo(Decimal64.NaN.Bits));
			Assert.That(Decimal64.Zero.Max(Decimal64.NaN).Bits, Is.EqualTo(Decimal64.NaN.Bits));
			Assert.That(Decimal64.NaN.Max(Decimal64.NaN).Bits, Is.EqualTo(Decimal64.NaN.Bits));
		}

		[Test]
		public void CompareTo()
		{
			Assert.That(Decimal64.FromDouble(1.0).CompareTo(Decimal64.Zero), Is.EqualTo(1.0.CompareTo(0.0)));
			Assert.That(Decimal64.Zero.CompareTo(Decimal64.Zero), Is.EqualTo(0.0.CompareTo(0.0)));
			Assert.That(Decimal64.Zero.CompareTo(Decimal64.FromDouble(1.0)), Is.EqualTo(0.0.CompareTo(1.0)));

			Assert.That(Decimal64.NaN.CompareTo(Decimal64.Zero), Is.EqualTo(Double.NaN.CompareTo(0.0)));
			Assert.That(Decimal64.Zero.CompareTo(Decimal64.NaN), Is.EqualTo(0.0.CompareTo(Double.NaN)));
			Assert.That(Decimal64.NaN.CompareTo(Decimal64.NaN), Is.EqualTo(Double.NaN.CompareTo(Double.NaN)));
		}

		[Test]
		public void TestSerializable()
		{
			var a1 = Decimal64.FromDouble(123.45);
			var b1 = Decimal64.FromLong(42);

			var formatter = new BinaryFormatter();
			var stream = new MemoryStream();
			formatter.Serialize(stream, a1);
			for (int i = 0; i < 40; ++i)
			{
				formatter.Serialize(stream, b1);
			}

			formatter.Serialize(stream, a1);

			//String l = stream.Length.ToString();
			//stream.Seek(0, SeekOrigin.Begin);
			//var fs = new FileStream("d:\\serialized.bin", FileMode.Create, FileAccess.Write);
			//stream.WriteTo(fs);
			//fs.Close();

			stream.Seek(0, SeekOrigin.Begin);

			Assert.True(a1.Equals(formatter.Deserialize(stream)));
			for (int i = 0; i < 40; ++i)
			{
				Assert.True(b1.Equals(formatter.Deserialize(stream)));
			}

			Assert.True(a1.Equals(formatter.Deserialize(stream)));
			stream.Close();
		}

		[Test]
		public void TestFromDecimalDoubleBasic()
		{
			Assert.AreNotEqual("9.2", Decimal64.FromDouble(9.2).ToString());
			Assert.AreEqual("9.199999999999999", Decimal64.FromDouble(9.2).ToString());
			Assert.AreEqual("9.2", Decimal64.FromDecimalDouble(9.2).ToString());
		}

		static void Main(string[] args)
		{
		}
	}
}
