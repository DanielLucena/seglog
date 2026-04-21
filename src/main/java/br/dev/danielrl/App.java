package br.dev.danielrl;

import br.dev.danielrl.server.entity.DistributedNode;
import br.dev.danielrl.server.entity.NodeFactory;
import br.dev.danielrl.server.protocol.CommunicationProtocol;
import br.dev.danielrl.server.protocol.ProtocolFactory;

public class App 
{
    public static void start(String[] args)
    {
        System.out.println();
        String protocolType = args.length > 0 ? args[0] : "udp";
        System.out.print( "Selected protocol: " + protocolType );

        String service = args.length > 1 ? args[1] : "gateway";
        System.out.print( ", Selected service: " + service );

        String portStr = args.length > 2 ? args[2] : "9000";
        int port = Integer.parseInt(portStr);
        System.out.println( ", Selected port: " + port );

        CommunicationProtocol protocol = ProtocolFactory.createProtocol(protocolType);

        DistributedNode node = NodeFactory.createNode(service, protocol, port);
        System.out.println(">_ Node created: " + node.getClass().getSimpleName() + " with protocol: " + protocol.getClass().getSimpleName());
        node.start();

        
    }
}
