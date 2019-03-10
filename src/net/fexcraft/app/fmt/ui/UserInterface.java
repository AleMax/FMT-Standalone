package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.FMTGLProcess;
import net.fexcraft.app.fmt.ui.generic.ControlsAdjuster;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.ui.generic.FileChooser;
import net.fexcraft.app.fmt.ui.generic.Menulist;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Time;

public class UserInterface {

	public static final float XSCALE = 1f, YSCALE = 1f;
	public static Element SELECTED = null;
	public static DialogBox DIALOGBOX;
	public static FileChooser FILECHOOSER;
	public static ControlsAdjuster CONTROLS;
	//
	private ArrayList<OldElement> oldelements = new ArrayList<>();
	private ArrayList<Element> elements = new ArrayList<>();
	private FMTGLProcess root;

	public UserInterface(FMTGLProcess main){
		this.root = main; root.setupUI(this);
	}
	
	private int width, height;
	private float[] clearcolor;

	public void render(boolean bool){
		width = root.getDisplayMode().getWidth(); height = root.getDisplayMode().getHeight();
		{
			GL11.glPushMatrix();
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
	        GL11.glOrtho(0, width, height, 0, -100, 100);
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
		}
		//
		GL11.glLoadIdentity();
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		if(bool){
			tmelm.render(width, height); logintxt.render(width, height);
		}
		else{
			for(OldElement elm : oldelements) elm.render(width, height);
			for(Element elm : elements) elm.render(width, height);
		}
		GL11.glDepthFunc(GL11.GL_LESS);
		//
		{
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPopMatrix();
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPopMatrix();
	        GL11.glDepthFunc(GL11.GL_LEQUAL);
	        //GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
	    	if(clearcolor == null){ clearcolor = Settings.background_color.toFloatArray(); }
	    	GL11.glClearColor(clearcolor[0], clearcolor[1], clearcolor[2], Settings.background_color.alpha);
	        GL11.glClearDepth(1.0);
	        GL11.glPopMatrix();
		}
	}
	
	private OldElement tmelm = new TextField(null, "text", 4, 4, 500){
		@Override
		public void renderSelf(int rw, int rh){
			this.y = rh - root.getDisplayMode().getHeight() + 4;
			this.setText((Time.getDay() % 2 == 0 ? "FMT - Fexcraft Modelling Toolbox" : "FMT - Fex's Modelling Toolbox") + (Static.dev() ? " [Developement Version]" : " [Standard Version]"), false);
			super.renderSelf(rw, rh);
		}
	};
	private OldElement logintxt = new TextField(null, "text", 4, 4, 500){
		@Override
		public void renderSelf(int rw, int rh){
			this.y = rh - root.getDisplayMode().getHeight() + 32;
			switch(FMTB.MODEL.creators.size()){
				case 0: {
					this.setText(FMTB.MODEL.name + " - " + (SessionHandler.isLoggedIn() ? SessionHandler.getUserName() : "Guest User"), false);
					break;
				}
				case 1: {
					if(FMTB.MODEL.creators.get(0).equals(SessionHandler.getUserName())){
						this.setText(FMTB.MODEL.name + " - by " + SessionHandler.getUserName(), false);
					}
					else{
						this.setText(FMTB.MODEL.name + " - by " + String.format("%s (logged:%s)", FMTB.MODEL.creators.get(0), SessionHandler.getUserName()), false);
					}
					break;
				}
				default: {
					if(FMTB.MODEL.creators.contains(SessionHandler.getUserName())){
						this.setText(FMTB.MODEL.name + " - by " + SessionHandler.getUserName() + " (and " + (FMTB.MODEL.creators.size() - 1) + " others)", false);
					}
					else{
						this.setText(FMTB.MODEL.name + " - " + String.format("(logged:%s)", SessionHandler.getUserName()), false);
					}
					break;
				}
			}
			super.renderSelf(rw, rh);
		}
	};

	public boolean isAnyHovered(){
		return oldelements.stream().filter(pre -> pre.anyHovered()).count() > 0;
	}

	public void onButtonPress(int i){
		if(Menulist.anyMenuHovered()){
			for(Menulist list : Menulist.arrlist){
				if(list.hovered && list.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, true)) return;
			}
		}
		else{
			OldElement eelm = null;
			for(OldElement elm : oldelements){
				if(elm.visible && elm.enabled /*&& elm.hovered*/){
					if(elm.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, elm.hovered)){
						return;
					} else eelm = elm;
				}
			}
			if(i == 0 && (eelm == null ? true : eelm.id.equals("toolbar"))){//TODO mostly obsolete check, but /shrug
				RayCoastAway.doTest(true, true);
			}
		}
	}

	public boolean onScrollWheel(int wheel){
		for(OldElement elm : oldelements){
			if(elm.visible && elm.enabled){
				if(elm.onScrollWheel(wheel)) return true;
			}
		} return false;
	}

	public OldElement getElement(String string){
		for(OldElement elm : oldelements) if(elm.id.equals(string)) return elm; return null;
	}

	public boolean hasElement(String string){
		return getElement(string) != null;
	}
	
	public ArrayList<OldElement> getOldElements(){ return oldelements; }
	
	public ArrayList<Element> getElements(){ return elements; }
	
}