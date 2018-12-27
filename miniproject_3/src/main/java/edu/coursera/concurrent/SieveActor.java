package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.finish;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        //throw new UnsupportedOperationException();
        final SieveActorActor sieveActorActor = new SieveActorActor();
        finish(() -> {
            for (int i = 3; i <= limit; i += 2) {
                sieveActorActor.send(i);
            }
            sieveActorActor.send(0);
        });

        int numPrimes = 1;
        SieveActorActor loopActor = sieveActorActor;
        while (loopActor != null) {
            numPrimes += loopActor.numLocalPrimes;
            loopActor = loopActor.nextActorActor;
        }
        return numPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        //throw new UnsupportedOperationException();
        static final int MAX_LOCAL_PRIMES = 1000;

        SieveActorActor nextActorActor = null;
        int localPrimes[] = new int[MAX_LOCAL_PRIMES];
        int numLocalPrimes = 0;
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;

            if (candidate <= 0) {
                if (nextActorActor != null) {
                    nextActorActor.send(msg);
                }
                return;
            }

            if (isLocalPrime(candidate)) {
                if (numLocalPrimes < MAX_LOCAL_PRIMES) {
                    localPrimes[numLocalPrimes++] = candidate;
                    return;
                }
                if (nextActorActor == null) {
                    nextActorActor = new SieveActorActor();
                }
                nextActorActor.send(candidate);
            }
        }

        boolean isLocalPrime(int candidate) {
            for (int i = 0; i < numLocalPrimes; i++) {
                if (candidate % localPrimes[i] == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
