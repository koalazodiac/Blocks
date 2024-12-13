package assignment3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Block {

	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outer most block) is at level 0
	private int maxDepth;
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}

	public static Random gen = new Random(2);


	/*
	 * These two constructors are here for testing purposes.
	 */
	public Block() {

	}

	public Block(int x, int y, int size, int lvl, int  maxD, Color c, Block[] subBlocks) {
		this.xCoord=x;
		this.yCoord=y;
		this.size=size;
		this.level=lvl;
		this.maxDepth = maxD;
		this.color=c;
		this.children = subBlocks;
	}


	/*
	 * Creates a random block given its level and a max depth.
	 *
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		boolean divided = false;
		this.level = lvl;
		this.maxDepth = maxDepth;
		// not equal to maxDepth because subblock = lvl+1
		if (lvl < maxDepth){
			this.color=null;
			double random = gen.nextDouble();
			if (random < Math.exp(-0.25 * lvl)){
				Block ur = new Block(lvl+1, maxDepth);
				Block ul = new Block(lvl+1, maxDepth);
				Block ll = new Block(lvl+1, maxDepth);
				Block lr = new Block(lvl+1, maxDepth);
				this.children = new Block[4];
				children[0] = ur;
				children[1] = ul;
				children[2] = ll;
				children[3] = lr;
				divided = true;
			}
		}
		if (! divided){
			this.children = new Block[0];
			int ran_color = gen.nextInt(4);
			this.color = assignment3.GameColors.BLOCK_COLORS[ran_color];
		}
	}


	/*
	  * Updates size and position for the block and all of its sub-blocks, while
	  * ensuring consistency between the attributes and the relationship of the
	  * blocks.
	  *
	  *  The size is the height and width of the block. (xCoord, yCoord) are the
	  *  coordinates of the top left corner of the block.
	 */
	public void updateSizeAndPosition (int size, int xCoord, int yCoord) {
		if (size <=0){
			throw new IllegalArgumentException();
		}
		int n = size;
		int lvl = this.level;
		while (lvl < this.maxDepth) {
			if (n % 2 != 0){
				throw new IllegalArgumentException();
			}
			n /= 2;
			lvl++;
		}
		this.size = size;
	  	this.xCoord = xCoord;
	  	this.yCoord = yCoord;
	  	if (this.children.length != 0){
		  	this.children[0].updateSizeAndPosition(size/2,xCoord+size/2,yCoord);
		  	this.children[1].updateSizeAndPosition(size/2,xCoord,yCoord);
		  	this.children[2].updateSizeAndPosition(size/2,xCoord,yCoord+size/2);
		  	this.children[3].updateSizeAndPosition(size/2,xCoord+size/2,yCoord+size/2);
	  	}
	}


	/*
  	* Returns a List of blocks to be drawn to get a graphical representation of this block.
  	*
  	* This includes, for each undivided Block:
  	* - one BlockToDraw in the color of the block
  	* - another one in the FRAME_COLOR and stroke thickness 3
  	*
  	* Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
  	*
  	* The order in which the blocks to draw appear in the list does NOT matter.
  	*/
	public ArrayList<assignment3.BlockToDraw> getBlocksToDraw() {
		ArrayList<assignment3.BlockToDraw> array = new ArrayList<assignment3.BlockToDraw>();
		if (this.children.length != 0){
			for (int i = 0; i<4; i++){
				array.addAll(this.children[i].getBlocksToDraw());
			}
		}
		else{
			array.add(new assignment3.BlockToDraw(this.color, this.xCoord, this.yCoord, this.size, 0));
			array.add(new assignment3.BlockToDraw(assignment3.GameColors.FRAME_COLOR, this.xCoord, this.yCoord, this.size, 3));
		}
		return array;
	}

	/*
	 * This method is provided and you should NOT modify it.
	 */
	public assignment3.BlockToDraw getHighlightedFrame() {
		return new assignment3.BlockToDraw(assignment3.GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}



	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than
	 * the lowest block at the specified location, then return the block
	 * at the location with the closest level value.
	 *
	 * The location is specified by its (x, y) coordinates. The lvl indicates
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will
	 * contain the location (x, y) too. This is why we need lvl to identify
	 * which Block should be returned.
	 *
	 * Input validation:
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	public Block getSelectedBlock(int x, int y, int lvl) {
		if (lvl>this.maxDepth || lvl<this.level){
			throw new IllegalArgumentException();
		}

		if (this.xCoord<=x && x < this.xCoord+this.size && this.yCoord <= y && y<this.yCoord+this.size){
			if (lvl == this.level){
				return this;
			}
			if (this.children.length == 0){
				return this;
			}
			else{
				for (int i = 0; i<4; i++){
					Block result = this.children[i].getSelectedBlock(x, y, lvl);
					if (result != null){
						return result;
					}
				}
			}
		}
		return null;
	}

	/*
	 * Swaps the child Blocks of this Block.
	 * If input is 1, swap vertically. If 0, swap horizontally.
	 * If this Block has no children, do nothing. The swap
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 *
	 */
	public void reflect(int direction) {
		if (direction == 0){
			if (this.children.length != 0){
				this.children[0].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord+this.size/2);
				this.children[1].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord+this.size/2);
				this.children[2].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord);
				this.children[3].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord);
				Block temp1 = this.children[0];
				Block temp2 = this.children[1];
				this.children[0]=this.children[3];
				this.children[1]=this.children[2];
				this.children[3]=temp1;
				this.children[2]=temp2;
				for (int i =0;i<4;i++){
					this.children[i].reflect(direction);
				}

			}
		}
		else if (direction == 1){
			if (this.children.length != 0) {
				this.children[0].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord);
				this.children[1].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord);
				this.children[2].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord+this.size/2);
				this.children[3].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord+this.size/2);
				Block temp1 = this.children[0];
				Block temp2 = this.children[3];
				this.children[0]=this.children[1];
				this.children[3]=this.children[2];
				this.children[1]=temp1;
				this.children[2]=temp2;
				for (int i =0;i<4;i++){
					this.children[i].reflect(direction);
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}



	/*
	 * Rotate this Block and all its descendants.
	 * If the input is 1, rotate clockwise. If 0, rotate
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		if (direction == 0){
			if (this.children.length != 0){
				this.children[0].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord);
				this.children[1].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord+this.size/2);
				this.children[2].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord+this.size/2);
				this.children[3].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord);
				Block temp = this.children[0];
				this.children[0]=this.children[3];
				this.children[3]=this.children[2];
				this.children[2]=this.children[1];
				this.children[1]=temp;

				for (int i =0;i<4;i++){
					this.children[i].rotate(direction);
				}
			}
		}
		else if (direction == 1){
			if (this.children.length != 0){
				this.children[0].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord+this.size/2);
				this.children[1].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord);
				this.children[2].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord);
				this.children[3].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord+this.size/2);
				Block temp = this.children[0];
				this.children[0]=this.children[1];
				this.children[1]=this.children[2];
				this.children[2]=this.children[3];
				this.children[3]=temp;

				for (int i =0;i<4;i++){
					this.children[i].rotate(direction);
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}



	/*
	 * Smash this Block.
	 *
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 *
	 * A Block can be smashed iff it is not the top-level Block
	 * and it is not already at the level of the maximum depth.
	 *
	 * Return True if this Block was smashed and False otherwise.
	 *
	 */
	public boolean smash() {
		if (this.level != 0 && this.level != this.maxDepth){
			this.color=null;
			this.children = new Block[4];
			this.children[0] = new Block(this.level+1, this.maxDepth);
			this.children[1] = new Block(this.level+1, this.maxDepth);
			this.children[2] = new Block(this.level+1, this.maxDepth);
			this.children[3] = new Block(this.level+1, this.maxDepth);

			this.children[0].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord);
			this.children[1].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord);
			this.children[2].updateSizeAndPosition(this.size/2, this.xCoord,this.yCoord+this.size/2);
			this.children[3].updateSizeAndPosition(this.size/2, this.xCoord+this.size/2,this.yCoord+this.size/2);
			return true;
		}
		return false;
	}


	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 *
	 * Return and array arr where, arr[i] represents the unit cells in row i,
	 * arr[i][j] is the color of unit cell in row i and column j.
	 *
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */
	public Color[][] flatten() {
		int unitsize = this.size;
		int lev = this.level;
		while (lev<this.maxDepth){
			unitsize=unitsize/2;
			lev++;
		}
		int as=this.size/unitsize; //actual size
		Color[][] result = new Color[as][as];
		for (int i = 0; i<as; i++){
			for (int j=0;j<as;j++){
				int l = this.level;
				while (l <= this.maxDepth && this.getSelectedBlock(this.xCoord+i*unitsize, this.yCoord+j*unitsize, l).color == null){
					l++;
				}
//				System.out.println(this.xCoord+i*unitsize);
//				System.out.println(this.yCoord+j*unitsize);
//				System.out.println(l);
				result[j][i] = this.getSelectedBlock(this.xCoord+i*unitsize,this.yCoord+j*unitsize, l).color;
			}
		}

		return result;
	}



	// These two get methods have been provided. Do NOT modify them.
	public int getMaxDepth() {
		return this.maxDepth;
	}

	public int getLevel() {
		return this.level;
	}


	/*
	 * The next 5 methods are needed to get a text representation of a block.
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i=0; i<indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = assignment3.GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);
		}
		else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}

	private static void coloredPrint(String message, Color color) {
		System.out.print(assignment3.GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(assignment3.GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock(){
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = assignment3.GameColors.colorToString(value).toUpperCase();
				if(colorName.length() == 0){
					colorName = "\u2588";
				}
				else{
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
	}
}
