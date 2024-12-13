package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal{

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] b = board.flatten();
		int length = b.length;
		boolean[][] visited = new boolean[length][length];
		//set visited to all false
		for (int i = 0; i < length; i++){
			for (int j = 0; j< length; j++){
				visited[i][j] = false;
			}
		}
		int result = 0;
		for (int i = 0; i < length; i++){
			for (int j = 0; j < length; j++) {
				int a = undiscoveredBlobSize(i, j, b, visited);
				if (a > result){
					result = a;
				}
			}
		}

		return result;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
				+ " blocks, anywhere within the block";
	}


	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		if (i < 0 || i >= unitCells.length || j < 0 || j >= unitCells[0].length ||
				visited[i][j] || unitCells[i][j] != super.targetGoal) {
			return 0;
		}
		visited[i][j] = true;
		int result = 1;
		if (i+1< unitCells.length) {
			result += undiscoveredBlobSize(i + 1, j, unitCells, visited);
		}
		if (i-1>=0){
			result += undiscoveredBlobSize(i - 1, j, unitCells, visited);
		}
		if (j+1< unitCells.length) {
			result += undiscoveredBlobSize(i, j + 1, unitCells, visited);
		}
		if (j-1>=0) {
			result += undiscoveredBlobSize(i, j - 1, unitCells, visited);
		}

		return result;

	}

}
