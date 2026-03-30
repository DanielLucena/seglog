package br.dev.danielrl;

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
