package strategy.decision;

import operator.Operator;

public class MultiDecision implements IDecision{
    @Override
    public void execute() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                
            }
        });
        thread.start();
    }
}
