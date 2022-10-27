package logic.test;

import logic.Symbolic.*;
import logic.tool.*;

public class testMain {
	public static void main(String[] args) {
		Tool t = new Tool();
		Call c = new Call();
		
		
		
		c.wait(10);
		//System.out.println(c.getCommand());
		
		
		
		Input in = new Input();
		
		in.down();
		in.thumbUp();
		in.littleUp();
		
		
		//System.out.println((in.getCommand()));
		//System.out.println((int)in.getCommand().charAt(0));
		
		//byte[] incommand = in.getCommand().charAt(0);
		
		//System.out.println(incommand[0]);
		
		
		Output out = new Output();
		
		out.setOutputID(40);
		out.eventIsAltitude();
		out.setMessage(" ƴϴ.");
		
		//System.out.println(out.getCommand());
		
		
		Assignment as = new Assignment();
		
		as.setAssignmentID(40);
		as.setValue(1);
		
		//System.out.println(as.getCommand());
		
		
		String s = "s" + (char)0 + (char)5 + (char)10 + (char)70;
		
		//System.out.println(s);
		
		
		StartEnd st = new StartEnd();
		StartEnd end = new StartEnd();
		
		st.setPreSymbol(null);
		st.setPostSymbol(c);
		
		c.setPostSymbol(in);
		
		in.setPostSymbol(out);
		
		Selection sl = new Selection();
		Condition cd = new Condition();
		
		Loop lp = new Loop();
		Condition cd2 = new Condition();
		
		out.setPostSymbol(lp.getLoopPoint());
		
		
		lp.getLoopPoint().setPostSymbol(sl);
		lp.getLoopPoint().setLoopPointID(80);;
		
		sl.setPostSymbol(lp);
		
		Call c1 = new Call();
		Call c2 = new Call();
		
		c1.takeOff();
		c1.tureEndFlagOn();
		c1.setPostSymbol(end);
		
		c2.landing();
		c2.falseEndFlagOn();
		c2.setPostSymbol(end);
		
		Call c3 = new Call();
		Call c4 = new Call();
		
		c3.takeOff();
		c3.tureEndFlagOn();
		c3.setPostSymbol(end);
		 
		c4.landing();
		c4.falseEndFlagOn();
		c4.setPostSymbol(end);
		
		cd.equel();
		
		Assignment ab = new Assignment();
		
		ab.setValue(2);
		
		cd.setFrontValue(ab);
		
		//cd2.setFrontValue(ab);
		cd2.equel();
		
		sl.setCondition(cd);
		sl.setTureStartSymbol(c1);
		sl.setFalseStartSymbol(c2);
		
		lp.setPostSymbol(as);
		
		lp.setCondition(cd2);
		lp.setTureStartSymbol(c3);
		lp.setFalseStartSymbol(c4);
		
		System.out.println(sl.getTureStartSymbol().getClass().getName());
		
		as.setPostSymbol(end);
		
		end.setPostSymbol(null);
		
		st.setCommand(t.makeCommand(st.getPostSymbol()) + "");
		
		
		String command = st.getCommand(); 
		
		System.out.println(command + '\n');
		
		for(int i = 0; i < command.length(); i++) {
			System.out.print(command.charAt(i));
			System.out.println("	" + (int)command.charAt(i));
		}
		
		
		try {
			t.commandExecution(command);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
