package br.dev.danielrl.server.heartbeat;

public abstract class AbstractFailureDetector {

    private HeartBeatScheduler heartbeatScheduler = new HeartBeatScheduler(this::heartBeatCheck, 100l);

    abstract void heartBeatCheck();

    abstract <T> void heartBeatReceived(T serverId);
}
