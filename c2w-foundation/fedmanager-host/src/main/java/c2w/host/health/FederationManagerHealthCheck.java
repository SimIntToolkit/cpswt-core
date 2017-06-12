package c2w.host.health;

import c2w.hla.FederationManager;
import com.codahale.metrics.health.HealthCheck;

public class FederationManagerHealthCheck extends HealthCheck {
    private final FederationManager federationManager;

    public FederationManagerHealthCheck(FederationManager federationManager) {
        this.federationManager = federationManager;
    }

    @Override
    protected Result check() throws Exception {
        if(this.federationManager.isRunning() || this.federationManager.isPaused()) {
            return Result.healthy();
        }
        else {
            return Result.unhealthy("FederationManager is not running or paused");
        }
    }
}
