import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
 
public class Main extends StateBasedGame
{
	
	public Main(String title)
	{
        super(title);
    }
	
    public static void main(String[] args) throws SlickException
    {
    	AppGameContainer app = new AppGameContainer(new Main("Full Circle Generator"));
        
        app.setDisplayMode(app.getScreenWidth() - 150, app.getScreenHeight() - 100, false);
        System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
        app.setTitle("Full Circle Generator");
        app.setTargetFrameRate(60);
        app.setShowFPS(false);
        app.setVSync(true);
        app.start();
    }
 
    @Override
    public void initStatesList(GameContainer container) throws SlickException
    {
    	this.addState(new Generator());
    }
 
}