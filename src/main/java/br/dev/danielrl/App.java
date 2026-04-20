package br.dev.danielrl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import br.dev.danielrl.server.entity.DistributedNode;
import br.dev.danielrl.server.entity.NodeFactory;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.ProtocolFactory;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        System.out.println( "Hello World!" );

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Local host address: " + localHost.getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("Unable to get local host address: " + e.getMessage());
        }

        String protocolType = args.length > 0 ? args[0] : "http";
        System.out.println( "Using protocol: " + protocolType );

        String service = args.length > 1 ? args[1] : "default-service";
        System.out.println( "Starting service: " + service );

        String portStr = args.length > 2 ? args[2] : "8080";
        int port = Integer.parseInt(portStr);
        System.out.println( "Using port: " + port );

        CommunicationProtocol protocol = ProtocolFactory.createProtocol(protocolType);

        DistributedNode node = NodeFactory.createNode(service, protocol, port);
        System.out.println(">_ Node created: " + node.getClass().getSimpleName() + " with protocol: " + protocol.getClass().getSimpleName());
        node.start();

        
    }
}
