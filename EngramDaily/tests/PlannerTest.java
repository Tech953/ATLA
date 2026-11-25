package dev.engram.daily;
public final class PlannerTest {
    private static void equal(double a,double b){if(Math.abs(a-b)>1e-9)throw new AssertionError(a+" != "+b);}
    private static void invalid(double h,int w,double a){try{Planner.projectedHours(h,w,a);throw new AssertionError("Invalid scenario accepted");}catch(IllegalArgumentException expected){}}
    public static void main(String[] args){
        equal(Planner.projectedHours(5,12,.8),48);
        equal(Planner.projectedHours(0,1,1),0);
        equal(Planner.projectedHours(168,520,1),87360);
        invalid(-1,12,.8);invalid(169,1,1);invalid(5,0,1);invalid(5,521,1);
        invalid(5,12,1.01);invalid(5,12,-.01);invalid(Double.NaN,12,.8);invalid(5,12,Double.POSITIVE_INFINITY);
        String p=Planner.system("Jade's biography");
        if(!p.contains("Jade's biography")||!p.contains("No tools or external actions")||!p.contains("not a conscious person"))throw new AssertionError("Prompt contract missing");
        System.out.println("PASS: arithmetic, invalid inputs, and prompt contract");
    }
}
