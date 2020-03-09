package com.halotroop.litecraft.util.noise;

import java.util.Random;

public final class OctaveSimplexNoise
{
	protected SimplexNoise[] samplers;
	private double clamp;
	private double spread, amplitudeLow, amplitudeHigh;

	public OctaveSimplexNoise(Random rand, int octaves)
	{ this(rand, octaves, 1D, 1D, 1D); }

	public OctaveSimplexNoise(Random rand, int octaves, double spread, double amplitudeHigh, double amplitudeLow)
	{
		this.samplers = new SimplexNoise[octaves];
		this.clamp = 1D / (1D - (1D / Math.pow(2, octaves)));
		for (int i = 0; i < octaves; ++i)
		{ samplers[i] = new SimplexNoise(rand.nextLong()); }
		this.spread = spread;
		this.amplitudeLow = amplitudeLow;
		this.amplitudeHigh = amplitudeHigh;
	}

	public double sample(double x, double y)
	{
		double amplSpread = 0.5D;
		double result = 0;
		for (SimplexNoise sampler : this.samplers)
		{
			result += (amplSpread * sampler.sample(x / (amplSpread * this.spread), y / (amplSpread * this.spread)));
			amplSpread *= 0.5D;
		}
		result = result * this.clamp;
		return result > 0 ? result * this.amplitudeHigh : result * this.amplitudeLow;
	}

	public double sample(double x, double y, double z)
	{
		double amplSpread = 0.5D;
		double result = 0;
		for (SimplexNoise sampler : this.samplers)
		{
			double divisor = amplSpread * this.spread;
			result += (amplSpread * sampler.sample(x / divisor, y / divisor, z / divisor));
			amplSpread *= 0.5D;
		}
		result = result * this.clamp;
		return result > 0 ? result * this.amplitudeHigh : result * this.amplitudeLow;
	}
}
