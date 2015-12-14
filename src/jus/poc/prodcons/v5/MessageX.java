package jus.poc.prodcons.v5;

import jus.poc.prodcons.Message;

/**
 * Created by matthieu on 06/12/15.
 */
public class MessageX implements Message {

    private String message;
    public MessageX(String message){
        this.message = message;
    }

    public String toString(){
        return message;
    }
	
}
