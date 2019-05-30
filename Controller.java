package graphics.ui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import graphics.shapes.SCircle;
import graphics.shapes.SCollection;
import graphics.shapes.SRectangle;
import graphics.shapes.SText;
import graphics.shapes.Shape;
import graphics.shapes.attributes.ColorAttributes;
import graphics.shapes.attributes.SelectionAttributes;
import graphics.shapes.ui.AddNewCircle;
import graphics.shapes.ui.AddNewPolygon;
import graphics.shapes.ui.AddNewRectangle;
import graphics.shapes.ui.AddNewText;
import graphics.shapes.ui.ShapesView;

public class Controller implements MouseListener, MouseMotionListener, KeyListener {
	private Object model;
	private View view;
	private Point point;
	private boolean mousein = true;
	private boolean ctrl = false;
	private SCollection copiedShapeC,sc;
	private Point loc_copy;
	private Point loc_new;

	public Controller(Object newModel){
		this.model = newModel;
	}
	
	public void setView(View view){
		this.view = view;
	}
	
	final public View getView(){
		return this.view;
	}
	
	public void setModel(Object model){
		this.model = model;
	}
	
	public Object getModel(){
		return this.model;
	}
	
	private void deselect(){
		SCollection sc = (SCollection) this.getModel();
		Iterator<Shape> i=sc.iterator();
		while(i.hasNext()){
			SelectionAttributes sa = (SelectionAttributes) i.next().getAttributes(SelectionAttributes.ID);
			sa.unselect();
		}
	}
	
	public void mousePressed(MouseEvent e){
		this.point = e.getPoint();
		if(!this.ctrl){
			deselect();
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(s.getBounds().contains(this.point)){//pour sélectionner un shape
					sa.selected();
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e){
		if(!this.ctrl){
			deselect();
			this.getView().updateUI();
		}
	}

	public void mouseClicked(MouseEvent e){
		boolean showMenuModif=false;
		sc = (SCollection) this.getModel();
		Iterator<Shape> i=sc.iterator();
		while(i.hasNext()){
			Shape s=i.next();
			SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
			if(s.getBounds().contains(this.point)){
				if(this.ctrl){
					sa.toggleSelection();
				}
				else{
					sa.selected();
				}
				if (e.getButton()==3){
					showMenuModif=true;
					//if the shape is not a collection -> show modification menu
					if(s.getClass()!=SCollection.class) {
						Class<?> shapeclass=s.getClass();
						//to determine the class of the shape that we have selected
						showMenuModif(e,shapeclass,s);
					}
				}	
			}
			else{
				if(!this.ctrl){
					sa.unselect();
				}
			}
		}
		
		// The add  shape menu is shown when we haven't selected a shape otherwise we'll show the modification menu
		if (e.getButton()==3 && showMenuModif==false)
	    {
	    	showMenu(e);
	    }
		this.getView().updateUI();
	}
		// the function needs 3 variables : the mouse event, the shape's class and the shape that we'll modify
		private void showMenuModif(MouseEvent e,Class<?> shapeclass,Shape s)
		{
			JPopupMenu menu = new JPopupMenu("Popup");
			Point pt=e.getPoint();
			JMenuItem modifyitem = new JMenuItem("Modify");
		    menu.add(modifyitem);
		    modifyitem.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  if(shapeclass==SText.class) {
		    		  new AddNewText(model, (ShapesView) view,pt,s);
		    	  }
		    	  else if(shapeclass==SRectangle.class) {
		    		  new AddNewRectangle(model, (ShapesView) view,pt,s );	  
		    	  }
		    	  else if(shapeclass==SCircle.class) {
		    		  new AddNewCircle(model, (ShapesView) view,pt,s);	  
		    	  }
		    	  else {
		    		  
		    		  new AddNewPolygon(model, (ShapesView) view,pt,s);	  
		    	  }
		    	  
		      }
		    });
	    	menu.show(e.getComponent(), e.getX(), e.getY());
		}
	
		// The AddNewShape{Circle|Rectangle|Text|Polygon} class is used for both creating a new shape or modifying an existing one ;
		// if we're creating a new one the last parameter is null otherwise it's the shape to modify
		private void showMenu(MouseEvent e)
		{
			JPopupMenu menu = new JPopupMenu("Popup");
			Point pt=e.getPoint();
			JMenuItem item = new JMenuItem("Add Circle");
		    item.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        new AddNewCircle(model, (ShapesView) view,pt,null);
		      }
		    });
		    menu.add(item);
		    
		    item = new JMenuItem("Add Rectangle");
		    item.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        new AddNewRectangle(model, (ShapesView) view,pt,null);	        
		      }
		    });
		    menu.add(item);
		    
		    item = new JMenuItem("Add Text");
		    item.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        new AddNewText(model, (ShapesView) view,pt,null);	        
		      }
		    });
		    menu.add(item);

			item = new JMenuItem("Add Polygon");
		    item.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  new AddNewPolygon(model, (ShapesView) view,pt,null);
		      }
		    });
		    menu.add(item);
	    	menu.show(e.getComponent(), e.getX(), e.getY());
		}

	
	public void mouseEntered(MouseEvent e){
		this.mousein=true;
	}

	public void mouseExited(MouseEvent e){
		this.mousein=false;
	}
	
	public Rectangle getBoundsSelected(){
		Rectangle r = new Rectangle();
		Iterator<Shape> i=sc.iterator();
		while(i.hasNext()){
			Shape s=i.next();
			SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
			if(sa.isSelected()){
				r = r.union(s.getBounds());
			}	
		}
		return r;
	}
	
	public void mouseDragged(MouseEvent evt){	
		sc = (SCollection) this.getModel();
			if(sc.getBounds().contains(this.point))
			{
				Iterator<Shape> i=sc.iterator();
				while(i.hasNext()){
					Shape s=i.next();
					SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
					if(sa.isSelected() && this.mousein && getBoundsSelected().contains(this.point))
					{
						s.translate(evt.getPoint().x-this.point.x, evt.getPoint().y-this.point.y);		
					}	
				}
			}	
			if(this.mousein == true) {
				this.point = evt.getPoint();
				this.view.updateUI();
			}
			this.getView().updateUI();
	}
	
	public void mouseMoved(MouseEvent evt){
		
	}
	
	
	public void keyTyped(KeyEvent evt){		

	}
	
	public void keyReleased(KeyEvent evt){ 
		this.ctrl=false;
	}


	public void keyPressed(KeyEvent evt){
		
		if (evt.getKeyCode()==KeyEvent.VK_R) {
			Random rand = new Random();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes("SelectionAttributes");
				if(sa.isSelected()){						
						Color randomColor = new Color(r, g, b);
						s.addAttributes(new ColorAttributes(true,true,randomColor,Color.BLUE));
						s.addAttributes(new SelectionAttributes());							
					}	
				}
				this.view.updateUI();
		}
		
		if (evt.getKeyCode()==KeyEvent.VK_L) {
			ArrayList<Shape> list = new ArrayList<Shape>(); 
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes("SelectionAttributes");
				if(sa.isSelected()){
						list.add(s);
					}	
				}
			if (list.size()==2) {
				this.loc_copy=list.get(0).getLoc();
				this.loc_new=list.get(1).getLoc();
				list.get(0).setLoc(loc_new);
				list.get(1).setLoc(loc_copy);
				loc_copy=null;
				loc_new=null;
			}
				this.view.updateUI();
		}


	if(evt.getKeyCode()==KeyEvent.VK_CONTROL) {
			this.ctrl=true;
		}
		if(evt.getKeyCode()==KeyEvent.VK_A) {
			if(!this.ctrl){
				sc = (SCollection) this.getModel();
				Iterator<Shape> i=sc.iterator();
				while(i.hasNext()){
					Shape s=i.next();
					SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
					if(!sa.isSelected()){
						sa.selected();
					}
				}
				this.getView().updateUI();
			}	
		}
		if(evt.getKeyCode()==KeyEvent.VK_U) {
			if(!this.ctrl){
				deselect();
				this.getView().updateUI();
			}
		}
		if(evt.getKeyCode()==KeyEvent.VK_UP) {
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected()){
						s.translate(0,-10);		
					}	
				}
				this.view.updateUI();
		}
		if(evt.getKeyCode()==KeyEvent.VK_DOWN) {
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected()){
						s.translate(0,10);		
					}	
				}
				this.view.updateUI();
		}
		if(evt.getKeyCode()==KeyEvent.VK_LEFT) {
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected()){
						s.translate(-10,0);		
					}	
				}
				this.view.updateUI();
		}
		if(evt.getKeyCode()==KeyEvent.VK_RIGHT) {
			sc = (SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()){
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected()){
						s.translate(10,0);		
					}	
				}
				this.view.updateUI();
		}

		if ((evt.getModifiers()==KeyEvent.CTRL_MASK) && ((evt.getKeyChar()=='+') || (evt.getKeyChar()=='-')) ) {
			sc=(SCollection) this.getModel();
			int defaultchangesize=5;
			if (evt.getKeyChar()=='-') defaultchangesize*=-1;
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext())
			{
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected())
				{
					s.changeSize(defaultchangesize);		
				}
			}
			this.view.updateUI();
		}
		
		if ((evt.getModifiers()==KeyEvent.CTRL_MASK) && ((evt.getKeyCode()==KeyEvent.VK_C) || (evt.getKeyCode()==KeyEvent.VK_X)) ) {
			sc=(SCollection) this.getModel();
			copiedShapeC=new SCollection();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext())
			{
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected())
				{
					copiedShapeC.add(s.copy());
					if (evt.getKeyCode()==KeyEvent.VK_X) {
						i.remove();
						sc.remove(s);
					}
				}
			}
			setModel(sc);
			this.view.updateUI();
		}

		if ((evt.getModifiers()==KeyEvent.CTRL_MASK) && (evt.getKeyCode()==KeyEvent.VK_V) && (copiedShapeC!=null)) {
			sc=(SCollection) this.getModel();
			Iterator<Shape> i=copiedShapeC.iterator();
			while(i.hasNext()) 
			{
				Shape s=i.next();
				if (s.getBounds().contains(this.point))
					// If the shape contains the last mouse click, the position of the pasted one will be translated 
					// so that they don't be superimposed
					s.translate(50,50);
				else 
					// To get the pasted shape in the position of the last mouse click
					s.translate(this.point.x-copiedShapeC.getLoc().x, this.point.y-copiedShapeC.getLoc().y);
				sc.add(s);
				setModel(sc);
			}
			this.view.updateUI();	
		}
		
		if((evt.getKeyCode()==KeyEvent.VK_DELETE)) {
			sc=(SCollection) this.getModel();
			Iterator<Shape> i=sc.iterator();
			while(i.hasNext()) {
				Shape s=i.next();
				SelectionAttributes sa = (SelectionAttributes) s.getAttributes(SelectionAttributes.ID);
				if(sa.isSelected()) {
					i.remove();
					sc.remove(s);
				}
			}
			setModel(sc);
			this.view.updateUI();
		}
		
		if(evt.getKeyCode()==17)
			this.ctrl=true;
	}	
}
