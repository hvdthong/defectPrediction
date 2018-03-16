package org.apache.tools.ant.taskdefs.optional.perforce;

import org.apache.tools.ant.*;

import java.util.Date;
import java.text.SimpleDateFormat;


/** P4Label - create a Perforce Label.
 *
 *  P4Label inserts a label into perforce reflecting the
 *  current client contents.
 *
 *  Label name defaults to AntLabel if none set.
 *
 * Example Usage: <P4Label name="MyLabel-${TSTAMP}-${DSTAMP}" desc="Auto Build Label" />
 *
 * @author <A HREF="mailto:leslie.hughes@rubus.com">Les Hughes</A>
 */
public class P4Label extends P4Base {

    protected String name;
    protected String desc;
    protected String lock;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
   
 	public void setLock(String lock)  {
    	this.lock = lock;
 	}
        
    public void execute() throws BuildException {
        log("P4Label exec:",Project.MSG_INFO);
        
        if(P4View == null || P4View.length() < 1) {
        }
        
        if(desc == null || desc.length() < 1) {
            log("Label Description not set, assuming 'AntLabel'", Project.MSG_WARN);
            desc = "AntLabel";
        }
        
		if(lock != null && !lock.equalsIgnoreCase("locked")) {
        	log("lock attribute invalid - ignoring",Project.MSG_WARN);
		}
        
        if(name == null || name.length() < 1) {
            SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd-hh:mm");
            Date now = new Date();
            name = "AntLabel-"+formatter.format(now);
            log("name not set, assuming '"+name+"'", Project.MSG_WARN);
        }
        
        
        String newLabel = 
            "Label: "+name+"\n"+
            "Description: "+desc+"\n"+
            "Options: unlocked\n"+
            "View: "+P4View+"\n";

        P4Handler handler = new P4HandlerAdapter() {
            public void process(String line) {
                log(line, Project.MSG_VERBOSE);
            }
        };

        handler.setOutput(newLabel);

        execP4Command("label -i", handler);
        
        execP4Command("labelsync -l "+name, new P4HandlerAdapter() {
            public void process(String line) {
                log(line, Project.MSG_VERBOSE);
            }
        });
        
        
        log("Created Label "+name+" ("+desc+")", Project.MSG_INFO);

        if (lock != null && lock.equalsIgnoreCase("locked"))  {
        
        	log("Modifying lock status to 'locked'",Project.MSG_INFO);

        	final StringBuffer labelSpec = new StringBuffer();
            
            
        	handler = new P4HandlerAdapter()  {
           		public void process(String line)  {
                	log(line, Project.MSG_VERBOSE);
                    
					if(util.match("/^Options:/",line)) {
   	                	line = "Options: "+lock;
					}
                    
                    labelSpec.append(line+"\n");
           		}
        	};
        
        	
            
			execP4Command("label -o "+name, handler);
            log(labelSpec.toString(),Project.MSG_DEBUG);

            log("Now locking label...",Project.MSG_VERBOSE);
			handler = new P4HandlerAdapter() {
				public void process(String line) {
					log(line, Project.MSG_VERBOSE);
				}
        	};

            handler.setOutput(labelSpec.toString());
			execP4Command("label -i", handler);
        }
        
        
    }

}
