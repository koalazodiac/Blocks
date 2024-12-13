package assignment3;

import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] b = board.flatten();
		int result = 0;
		for (int i = 0; i < b.length; i++){
			if (b[i][0] == super.targetGoal){
				result +=1;
			}
			if (b[0][i] == super.targetGoal){
				result +=1;
			}
			if (b[b.length-1][i] == super.targetGoal){
				result +=1;
			}
			if (b[i][b.length-1] == super.targetGoal){
				result +=1;
			}
		}
		return result;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal)
				+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
