using System;
using System.Diagnostics;

namespace EPAM.Deltix.DFP
{
	public class SimpleBenchmark
	{
		public static void Run()
		{
			//const int N = 8000000;
			//const int M = 30;
			//Stopwatch t = new Stopwatch();
			//double Best = 1E99, Total = 0;
			//long y = 0; // dummy value
			//for (int j = 0; j < M; ++j)
			//{
			//	long x = (y & 2) | 1;
			//	t.Restart();
			//	for (int i = 0; i < N; ++i)
			//	{
			//		x = (Int64)NativeImpl.fromInt64(x) & 0xF | 1;
			//	}
			//	t.Stop();
			//	if (t.Elapsed.TotalSeconds < Best)
			//	{
			//		Best = t.Elapsed.TotalSeconds;
			//		y = x;
			//	}

			//	Total += t.Elapsed.TotalSeconds;
			//}

			//Console.WriteLine("Time Elapsed: " + Total + " s Iteration cost: " + 1.0E9 / N * Best + " ns");
			//Console.WriteLine(y);
		}
	}
}

namespace EPAM.Deltix.DFP.Demo
{
    class Program
    {
		// 
		static Decimal ToDecimal(Decimal64 x)
		{
			return (Decimal)x.ToDouble();
		}

		static Decimal64 ToDfp64(Decimal x)
		{
			return Decimal64.FromDouble((double)x);
		}

		static void TestToString()
		{
			for (int i = 0; i <= 10; ++i)
				Console.WriteLine(Decimal64.FromFixedPoint(0, -i));

			for (int i = 1; i <= 10; ++i)
				Console.WriteLine(Decimal64.FromFixedPoint(0, i));

			for (int i = 0; i <= 10; ++i)
				Console.WriteLine(Decimal64.FromFixedPoint(12, -i));

			for (int i = 1; i <= 10; ++i)
				Console.WriteLine(Decimal64.FromFixedPoint(12, i));

			for (int i = 0; i <= 10; ++i)
				Console.WriteLine(Decimal64.FromFixedPoint(12000, i));

			for (int i = 0; i <= 16; ++i)
				Console.WriteLine(Decimal64.FromFixedPoint(9999999999999999, i));


			Console.WriteLine(Decimal64.FromFixedPoint(9999999999999999, 383));
			Console.WriteLine(Decimal64.FromFixedPoint(9999999999999999, -369));
			Console.WriteLine("------");
			Console.WriteLine((-1.0 / 0.0).ToString(System.Globalization.CultureInfo.InvariantCulture));
			Console.WriteLine((-0.0 / 0.0).ToString(System.Globalization.CultureInfo.InvariantCulture));
			Console.WriteLine(Decimal64.PositiveInfinity);
			Console.WriteLine(Decimal64.NegativeInfinity);
			Console.WriteLine(Decimal64.NaN);
			Console.WriteLine(-Decimal64.NaN);

			Console.WriteLine("Dfp64.MAX: " + Decimal64.MaxValue);
			Console.WriteLine("strlen(Dfp64.MAX): " + Decimal64.MaxValue.ToString().Length);
			Console.WriteLine("Dfp64.MIN: " + Decimal64.MinValue);
			Console.WriteLine("strlen(Dfp64.MIN): " + Decimal64.MinValue.ToString().Length);
			Console.WriteLine("------");
		}

		static void TestOldDecimal()
		{
			Decimal64 x = Decimal64.FromULong(1234567890123451);
			Decimal64 y = Decimal64.FromDouble(1234567890123455);

			Console.WriteLine("Dfp64 x(intended): " + 1234567890123451);
			Console.WriteLine("Dfp64 y(intended): " + 1234567890123455);
			Console.WriteLine("Dfp64 x: " + x);
			Console.WriteLine("Dfp64 y: " + y);
			Console.WriteLine("Dfp64 x.ToDecimal(): " + x.ToDecimal());
			Console.WriteLine("Dfp64 y.ToDecimal(): " + y.ToDecimal());

			Console.WriteLine(ToDecimal(x));
			Console.WriteLine(ToDecimal(y));

			Console.WriteLine("x - y: " + x.Subtract(y));
			
			x = x.ScaleByPowerOfTen(10);
			y = y.ScaleByPowerOfTen(10);
			Console.WriteLine("x *= 1E10; y *= 1E10; ");

			Console.WriteLine("Dfp64 x: " + x);
			Console.WriteLine("Dfp64 y: " + y);
			Console.WriteLine("Dfp64 x.ToDecimal(): " + x.ToDecimal());
			Console.WriteLine("Dfp64 y.ToDecimal(): " + y.ToDecimal());
			Console.WriteLine("x - y: " + x.Subtract(y));

			Console.WriteLine("x *= -1E20; y *= -1E30; ");
			x = x.ScaleByPowerOfTen(-20);
			y = y.ScaleByPowerOfTen(-20);

			Console.WriteLine("Dfp64 x: " + x);
			Console.WriteLine("Dfp64 y: " + y);
			Console.WriteLine("Dfp64 x.ToDecimal(): " + x.ToDecimal());
			Console.WriteLine("Dfp64 y.ToDecimal(): " + y.ToDecimal());
			Console.WriteLine("x - y: " + x.Subtract(y));

			Console.WriteLine("x *= -1E10; y *= -1E10; ");
			x = x.ScaleByPowerOfTen(-10);
			y = y.ScaleByPowerOfTen(-10);

			Console.WriteLine("Dfp64 x: " + x);
			Console.WriteLine("Dfp64 y: " + y);
			Console.WriteLine("Dfp64 x.ToDecimal(): " + x.ToDecimal());
			Console.WriteLine("Dfp64 y.ToDecimal(): " + y.ToDecimal());
			Console.WriteLine("x - y: " + x.Subtract(y));
		}

        static int Main(string[] args)
        {
			if (args.Length == 0)
			{
				//TestToString();
				//TestOldDecimal();
				SimpleBenchmark.Run();
				Double binary64 = new Random().NextDouble();
				Decimal64 decimal64 = Decimal64.FromDouble(binary64);
				Console.WriteLine(decimal64);
				return 0;
			}
			else
			{
				if (args.Length != 3)
				{
					Console.Error.WriteLine("Usage: <A> <op> <B>");
					return 1;
				}

				var argA = Decimal64.Parse(args[0]);
				var argB = Decimal64.Parse(args[2]);
				var result = processOperation(args[1], argA, argB);

				Console.WriteLine(argA + "(=" + argA.ToUnderlying() + ") " + args[1] + " " +
					argB + "(=" + argB.ToUnderlying() + ") = " +
					result + "(=" + result.ToUnderlying() + ")");

				return 0;
			}
		}

		private static Decimal64 processOperation(string operation, Decimal64 argA, Decimal64 argB)
		{
			switch (operation)
			{
				case "+":
					return argA.Add(argB);
				case "-":
					return argA.Subtract(argB);
				case "*":
					return argA.Multiply(argB);
				case "/":
					return argA.Divide(argB);
				default:
					throw new ArgumentException($"Unsupported operation '{operation}'.");
			}
		}
	}
}
