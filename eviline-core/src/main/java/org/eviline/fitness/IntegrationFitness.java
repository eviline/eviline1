package org.eviline.fitness;

import java.util.Arrays;

import org.eviline.Block;
import org.eviline.Field;
import org.eviline.fitness.EvilineFitness.Weights;

public class IntegrationFitness extends DefaultFitness {
	@Override
	public double score(Field field) {
		if (field.isGameOver())
			return Double.POSITIVE_INFINITY;
		Block[][] f = field.getField();
		int[] stackHeight = new int[Field.WIDTH];
		for (int x = Field.BUFFER; x < Field.WIDTH + Field.BUFFER; x++) {
			for (int y = Field.HEIGHT + Field.BUFFER - 1; y >= 2; y--) {
				int h = Field.HEIGHT + Field.BUFFER - y + 1;
				Block b = f[y][x];
				if (b != null)
					stackHeight[x - Field.BUFFER] = h;
			}
		}

		int[] a = stackHeight;
		a = Arrays.copyOf(a, a.length + 1);
		System.arraycopy(a, 0, a, 1, a.length - 1);

		double c = 0; // returned penalty
		int o = 0, s = 0, z = 0; // number of spots for O, S, Z pieces

		int min = 20, max = 0, sum = 0;
		for (int i = 1; i <= 10; i++) {
			sum += a[i];
			if (min > a[i])
				min = a[i];
			if (max < a[i])
				max = a[i];
		}
		c += 0.5 * (sum / 10 - min); // too many blocks penalty

		if (a[1] < a[2] + 2 && a[9] + 2 > a[10])
			c += 3;// 2 wells penalty
		else if (a[1] <= a[2] + 2 && a[9] + 2 >= a[10])
			c += 1;

		for (int i = 1; i <= 8; i++) {
			if (Math.abs(a[i + 1] - a[i]) > 2 && Math.abs(a[i + 2] - a[i + 1]) > 2) {
				if (a[i] < a[i + 1] && a[i + 1] > a[i + 2])
					c += 2;// obelisc penalty
				else if (a[i] > a[i + 1] && a[i + 1] < a[i + 2])
					c += 3;// trench penalty
				else
					c += 1;// cliff penalty
			}
		}

		for (int i = 1; i <= 9; i++) {
			int d = Math.abs(a[i + 1] - a[i]);
			if (d > 2)
				c += d - 1; // local height difference penalty
			else if (a[i + 1] == a[i] - 2)
				c += 1;
			else if (a[i + 1] == a[i] + 2)
				c += 1;
			else if (a[i + 1] == a[i] - 1)
				s += 1;
			else if (a[i + 1] == a[i] + 1)
				z += 1;
			else {
				o += 1;
				if (i + 2 <= 10 && a[i + 2] == a[i] + 1)
					s += 1;
				if (i - 1 >= 1 && a[i - 1] == a[i] + 1)
					z += 1;
			}
		}

		if (o == 0)
			c += 20;// no O spot penalty
		else if (o == 1)
			c += 5;// just one O spot penalty
		else if (o == 2)
			c += 2;// just two O spots penalty
		else if (o == 3)
			c += 1;// just two O spots penalty
		if (s == 0)
			c += 15;// no S spot penalty
		else if (s == 1)
			c += 3;// just one S spot penalty
		if (z == 0)
			c += 15;// no Z spot penalty
		else if (z == 1)
			c += 3;// just one Z spot penalty

		int w = 0;// number of white chess fields
		for (int i = 1; i <= 10; i++) {
			w += i + a[i] % 2;
		}
		if (Math.abs(w - 5) >= 2)
			c += 1 + 7 * (Math.abs(w - 5) - 2); // bad T placement penalty

		for (int i = 1; i <= 6; i++) {
			w = 0;
			w += (i + a[i]) % 2;
			w += (i + 1 + a[i + 1]) % 2;
			w += (i + 2 + a[i + 2]) % 2;
			w += (i + 3 + a[i + 3]) % 2;
			w += (i + 4 + a[i + 4]) % 2;
			if (w == 0 || w == 5)
				c += 2;// local imbalance penalty
			else if (w == 1 || w == 4)
				c += 1;
		}
		w = (1 + a[1]) % 2;
		w += (2 + a[2]) % 2;
		w += (3 + a[3]) % 2;
		w += (4 + a[4]) % 2;
		if (w == 0 || w == 4)
			c += 1; // left side imbalance penalty
		w = (7 + a[7]) % 2;
		w += (8 + a[8]) % 2;
		w += (9 + a[9]) % 2;
		w += (10 + a[10]) % 2;
		if (w == 0 || w == 4)
			c += 1; // right side imbalance penalty

		return c;

	}

	@Override
	public void paintImpossibles(Field field) {
		paintImpossibles(field.getField());
	}

	public void paintImpossibles(Block[][] f) {
		for (int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if (f[y][x] == null)
					f[y][x] = Block.X;
			}
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if ((f[y - 1][x] == null || f[y][x - 1] == null || f[y][x + 1] == null)
						&& f[y][x] == Block.X)
					f[y][x] = null;
			}
			for (int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if ((f[y - 1][x] == null || f[y][x - 1] == null || f[y][x + 1] == null)
						&& f[y][x] == Block.X)
					f[y][x] = null;
			}
		}
	}

	@Override
	public void paintUnlikelies(Field field) {
		paintUnlikelies(field.getField());
	}

	public void paintUnlikelies(Block[][] f) {
		for (int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if (f[y][x] == null && f[y][x - 1] != null
						&& f[y][x + 1] != null) {
					f[y][x] = Block.G;
				}
			}
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if (f[y][x] != null)
					continue;
				if (f[y - 1][x] == Block.G || f[y][x - 1] == Block.G)
					f[y][x] = Block.G;
			}
			for (int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if (f[y][x] != null)
					continue;
				if (f[y - 1][x] == Block.G || f[y][x + 1] == Block.G)
					f[y][x] = Block.G;
			}
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if (f[y][x] != Block.G)
					continue;
				if (f[y][x + 1] == null || f[y][x - 1] == null)
					f[y][x] = null;
			}
			for (int x = Field.BUFFER + Field.WIDTH - 1; x >= Field.BUFFER; x--) {
				if (f[y][x] != Block.G)
					continue;
				if (f[y][x + 1] == null || f[y][x - 1] == null)
					f[y][x] = null;
			}
		}
	}

	@Override
	public void unpaintUnlikelies(Field field) {
		Block[][] f = field.getField();
		for (int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if (f[y][x] == Block.G)
					f[y][x] = null;
			}
		}
	}

	@Override
	public void unpaintImpossibles(Field field) {
		Block[][] f = field.getField();
		for (int y = 1; y < Field.BUFFER + Field.HEIGHT; y++) {
			for (int x = Field.BUFFER; x < Field.BUFFER + Field.WIDTH; x++) {
				if (f[y][x] == Block.X)
					f[y][x] = null;
			}
		}
	}

}
