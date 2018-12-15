package microservices;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Theodore Chaikalis
 */
public class PipelineTest {
    
    
    @Test
    public void pipeline1(){
        CompletableFuture.supplyAsync(this::message)
                .thenAccept(this::consumeMessage)
                .thenRun(this::finalAction);     
    }
    
        @Test
    public void pipeline2_DataTransformations(){
        CompletableFuture.supplyAsync(this::message)
                .thenApply(this::beautify)
                .thenAccept(this::consumeMessage)
                .thenRun(this::finalAction);     
    }
    
    @Test
    public void combiningPipelines(){
        CompletableFuture<String> first = CompletableFuture.supplyAsync(this::message);
        CompletableFuture<String> second = CompletableFuture.supplyAsync(this::greetings);
                
        first.thenCombine(second, this::combinator).thenApply(this::beautify)
                .thenAccept(this::consumeMessage);
    }
    
    @Test
    public void combiningPipelinesWithSeparateSlowBeutify() throws InterruptedException, ExecutionException{
        CompletableFuture<String> first = CompletableFuture.supplyAsync(this::message).thenApply(this::beautifySlow);
        CompletableFuture<String> second = CompletableFuture.supplyAsync(this::greetings).thenApply(this::beautifySlow);
                
        first.thenCombine(second, this::combinator)
                .thenAccept(this::consumeMessage).get();
    }
    
     @Test
    public void combiningPipelinesWithSeparateSlowButAsyncBeutify() throws InterruptedException, ExecutionException{
        CompletableFuture<String> first = CompletableFuture.supplyAsync(this::message).thenApplyAsync(this::beautifySlow);
        CompletableFuture<String> second = CompletableFuture.supplyAsync(this::greetings).thenApplyAsync(this::beautifySlow);
                
        first.thenCombine(second, this::combinator)
                .thenAccept(this::consumeMessage).get();
    }
    
    @Test
    public void composingPipelines(){
        CompletableFuture.supplyAsync(this::message)
                .thenCompose(this::compose)
                .thenAccept(this::consumeMessage);
    }
            
    CompletionStage<String> compose(String input){
        return CompletableFuture.supplyAsync(() -> input)
                .thenApply(this::beautifySlow);
    }
    
    
    String greetings(){
        return "Good morning dear!";
    }
    
    String combinator(String first, String second){
        return first + " ---- " +second;
    }
    
    String message(){
        return "Hey teo!! Time is " + System.currentTimeMillis();
    }
    
    void consumeMessage(String message){
        System.out.println("message = "+message);
    }
    
    void finalAction(){
        System.out.println("Finally! Clean Up!");
    }
    
    String beautify(String input){
        return "#### " + input + " ####";
    }
    
    String beautifySlow(String input){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PipelineTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "$$$ " + input + " $$$";
    }
    
    
            

}
