import java.awt.Font;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Generator extends BasicGameState
{
	Font font;
	TrueTypeFont wordFont;
	
	int windowWidth;
	int windowHeight;
	
	RadarCircles rc;
	Selector selector;
	
	String fileName = "Do I Wanna Know - Arctic Monkeys";
	Music song;
	boolean start;
	BufferedWriter rhythm;
	
	boolean[] keyBools;
	String nextCircleToWrite;
	
	boolean paused;
	String pausedString;
	
	String lastCircleWritten;

	@Override
	public void init(GameContainer gc, StateBasedGame state) throws SlickException
	{
		font = new Font("Courier", Font.BOLD, 20);
		wordFont = new TrueTypeFont(font, false);
		
		windowWidth = gc.getWidth();
		windowHeight = gc.getHeight();
		
		selector = new Selector();
		rc = new RadarCircles(windowWidth, windowHeight);
		
		song = new Music("data/" + fileName + ".wav");
		try {rhythm = new BufferedWriter(new FileWriter("data/" + fileName + ".txt"));} catch (IOException e) {}
		start = true;
		
		keyBools = new boolean[5];
		for (int i = 0; i < keyBools.length; i++)
			keyBools[i] = false;
		nextCircleToWrite = "";
		
		paused = true;
		pausedString = "Generator is paused. Press Escape to resume and w to write the current rhythm to file.";
		
		lastCircleWritten = "";
	}

	@Override
	public void render(GameContainer gc, StateBasedGame state, Graphics g) throws SlickException
	{
		g.setAntiAlias(true);
		g.setFont(wordFont);
		
		g.setColor(new Color(84, 84, 84));
		g.fill(new Rectangle(0, 0, gc.getWidth(), gc.getHeight()));
		
		g.setColor(Color.white);
		g.drawString(Float.toString(selector.getAngle()), 0, 0);
		g.drawString(Integer.toString(selector.getRotations()), 0, 20);
		
		rc.draw(g);
		
		if (paused)
		{
			g.setColor(Color.white);
			g.drawString(pausedString, gc.getWidth()/2 - g.getFont().getWidth(pausedString)/2, gc.getHeight()/2 - g.getFont().getHeight(pausedString)/2);
		}
		
		g.drawString("Written to file: " + lastCircleWritten, gc.getWidth() - g.getFont().getWidth("Written to file: " + lastCircleWritten), g.getFont().getHeight("Written to file: " + lastCircleWritten)/2);
	}
	
	@Override
	public void keyPressed(int key, char c)
	{
		if (key == Input.KEY_J)
		{
			if (!keyBools[0] && !keyBools[4])
			{
				keyBools[2] = true;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",3)";
			}
			else if (keyBools[0])
			{
				keyBools[1] = true;
				keyBools[0] = false;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",2)";
			}
			else if (keyBools[4])
			{
				keyBools[3] = true;
				keyBools[4] = false;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",4)";
			}
		}
		
		if (key == Input.KEY_H)
		{
			if (!keyBools[2])
			{
				keyBools[0] = true;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",1)";
			}
			else
			{
				keyBools[1] = true;
				keyBools[2] = false;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",2)";
			}
		}
		
		if (key == Input.KEY_K)
		{
			if (!keyBools[2])
			{
				keyBools[4] = true;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",5)";
			}
			else
			{
				keyBools[3] = true;
				keyBools[2] = false;
				nextCircleToWrite = "(" + (selector.getAngle()) + "," + selector.getRotations() + ",4)";
			}
		}
	}
	
	@Override
	public void keyReleased(int key, char c)
	{
		if (keyBools[0] || keyBools[1] || keyBools[2] || keyBools[3] || keyBools[4])
		{
			if (key == Input.KEY_H || key == Input.KEY_J || key == Input.KEY_K)
			{
				try {rhythm.write(nextCircleToWrite); rhythm.newLine();} catch (IOException e) {System.out.println(e);}
				lastCircleWritten = nextCircleToWrite;
				for (int i = 0; i < keyBools.length; i++)
					keyBools[i] = false;
			}
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame state, int delta) throws SlickException
	{
		Input input = gc.getInput();
		
		if (input.isKeyPressed(Input.KEY_ESCAPE))
		{
			if (paused)
			{
				if (start)
				{
					selector.start();
					song.play();
					start = false;
				}
				else
					song.resume();
			}
			else
				song.pause();
			paused = !paused;
		}
		
		selector.updateSelector();
		
		if (!paused)
		{
			if (input.isKeyDown(Input.KEY_H) && input.isKeyDown(Input.KEY_J))
			{
				rc.keyPressed(2);
			}
			else if (input.isKeyDown(Input.KEY_J) && input.isKeyDown(Input.KEY_K))	
			{
				rc.keyPressed(4);
			}
			else if (input.isKeyDown(Input.KEY_H))
			{
				rc.keyPressed(1);
			}
			else if (input.isKeyDown(Input.KEY_J))
			{
				rc.keyPressed(3);
			}
			else if (input.isKeyDown(Input.KEY_K))
			{
				rc.keyPressed(5);
			}
		}
		else
		{
			if (input.isKeyPressed(Input.KEY_W))
			{
				try{rhythm.close();} catch (IOException e) {System.out.println(e);}
				pausedString += "(rhythm was written to file)";
			}
		}
	}

	@Override
	public int getID()
	{
		return 0;
	}

}
