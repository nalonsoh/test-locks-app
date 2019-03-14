package hello;

import java.util.concurrent.locks.StampedLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// De la Documentación de Spring: "@EnableDiscoveryClient is no longer required. You can put a DiscoveryClient implementation on the classpath to cause the Spring Boot application to register with the service discovery server."
//@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }
    
}

@RestController
class ServiceInstanceRestController {
	private static final Log LOGGER = LogFactory.getLog(ServiceInstanceRestController.class);

    
    private final StampedLock lock = new StampedLock();
    
    private long lockCount = 0;

    @RequestMapping("/read-lock")
    public String getReadLock() {
    	String msg = "";
    	Thread.currentThread().setName("read-lock");
    	System.out.println("R - Thread Name: " + Thread.currentThread().getName());
    	
    	long stamp = this.lock.readLock();
    	System.out.println("read-lock adquirido");
    	try {
    		msg = "Read Lock acquired " + ++this.lockCount + "(" + this.lock.getReadLockCount() + ")";
//    		sleep(2000);
    		for (int i = 0; i <= 1000000; i++) {
    			if(i%1000 == 0) {
    				System.out.println();
    			}
				System.out.print(i +",");
				
			}
    	} finally {
    		if (0 != stamp) {
    			this.lock.unlock(stamp);
    			System.out.println("read-lock liberado");
    		}
    	}
    	
    	return msg;
    }
    
    @RequestMapping("/write-lock")
    public String getWriteLock() {
    	String msg = "";
    	System.out.println("W - Thread Name: " + Thread.currentThread().getName());
    	
    	long stamp = this.lock.writeLock();
    	System.out.println("write-lock adquirido");
    	try {
    		msg = "Write Lock acquired " + ++this.lockCount + "(lock: " + this.lock.getReadLockCount() + ")";
    		sleep(4000);
    	} finally {
    		if (0 != stamp) {
    			this.lock.unlock(stamp);
    			System.out.println("write-lock liberado");
    		}
		}
    	
    	return msg;
    }
    
    
    // Métdos de utilidad
    private static void sleep(long milis) {
		try {
			Thread.sleep(milis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    
}
