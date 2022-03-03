package org.structures;

import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;


public class RectArc {
	public double X1;
	public double Y1;
	public double X2;
	public double Y2;
	public String word;
	public Color color;
	public Line lu, ld, ll, lr;
	public boolean isRemoved = false;

	public static Color ColorList[] = null;

	public RectArc(double x1, double y1, double x2, double y2, Color c) {
		X1 = x1;
		Y1 = y1;
		X2 = x2;
		Y2 = y2;
		word = null;
		color = c;
	}

	public RectArc(double x1, double y1, double x2, double y2, String OCR, Color c) {
		X1 = x1;
		Y1 = y1;
		X2 = x2;
		Y2 = y2;
		word = OCR;
		color = c;
	}

	public void initializeLines(Pane father,int flag){
		if(flag == 0){
			lu = new Line(X1, Y1, X2, Y1);
			lu.setStroke(color);
			ld = new Line(X1, Y2, X2, Y2);
			ld.setStroke(color);
			lr = new Line(X2, Y1, X2, Y2);
			lr.setStroke(color);
			ll = new Line(X1, Y1, X1, Y2);
			ll.setStroke(color);
		}
		else{
			lu = new Line(X1, Y1, X2, Y1);
			lu.setStroke(Color.RED);
			lu.setStrokeWidth(2);
			ld = new Line(X1, Y2, X2, Y2);
			ld.setStroke(Color.RED);
			ld.setStrokeWidth(2);
			lr = new Line(X2, Y1, X2, Y2);
			lr.setStroke(Color.RED);
			lr.setStrokeWidth(2);
			ll = new Line(X1, Y1, X1, Y2);
			ll.setStroke(Color.RED);
			ll.setStrokeWidth(2);
		}
		father.getChildren().add(lu);
		father.getChildren().add(ld);
		father.getChildren().add(lr);
		father.getChildren().add(ll);
	}

	public void resetLines(){
		lu.setStartX(X1);
		lu.setStartY(Y1);
		lu.setEndX(X2);
		lu.setEndY(Y1);

		ld.setStartX(X1);
		ld.setStartY(Y2);
		ld.setEndX(X2);
		ld.setEndY(Y2);

		lr.setStartX(X2);
		lr.setStartY(Y1);
		lr.setEndX(X2);
		lr.setEndY(Y2);

		ll.setStartX(X1);
		ll.setStartY(Y1);
		ll.setEndX(X1);
		ll.setEndY(Y2);

	}

	public void removeLines(Pane father){
		if(lu != null)
			lu.setVisible(false);
		if(lu != null)
			lr.setVisible(false);
		if(lu != null)
			ll.setVisible(false);
		if(lu != null)
			ld.setVisible(false);
		father.getChildren().removeAll(lu, ld, lr, ll);
		lu = null;
		ld = null;
		lr = null;
		ll = null;
	}

	public void initializeWord(ListView listView, int index){
		if(word != null)
			//listView.getItems().add(word);
			listView.getItems().add(index + "." + word);
	}

	public String getWord(){
		if(word != null){
			return word;
		}
		else{
			return "";
		}
	}

	public void setWord(String str){
		word = str;
	}

	public void remove(){
		if(lu != null)
			lu.setVisible(false);
		if(lu != null)
			lr.setVisible(false);
		if(lu != null)
			ll.setVisible(false);
		if(lu != null)
			ld.setVisible(false);
		isRemoved = true;
	}

}