/*
 * Decompiled with CFR 0.150.
 */
package anon.infoservice;

import anon.infoservice.IProxyInterfaceGetter;
import anon.infoservice.ImmutableProxyInterface;
import anon.infoservice.ProxyInterface;

public interface IMutableProxyInterface {
    public IProxyInterfaceGetter getProxyInterface(boolean var1);

    public static class DummyMutableProxyInterface
    implements IMutableProxyInterface {
        private ProxyInterface m_proxy;
        private IProxyInterfaceGetter m_anonymousGetter = null;
        private IProxyInterfaceGetter m_dummyGetter = new IProxyInterfaceGetter(){

            public ImmutableProxyInterface getProxyInterface() {
                return DummyMutableProxyInterface.this.m_proxy;
            }
        };

        public DummyMutableProxyInterface() {
            this.m_proxy = null;
        }

        public DummyMutableProxyInterface(ProxyInterface proxyInterface) {
            this.m_proxy = proxyInterface;
        }

        public DummyMutableProxyInterface(IProxyInterfaceGetter iProxyInterfaceGetter, IProxyInterfaceGetter iProxyInterfaceGetter2) {
            this.m_dummyGetter = iProxyInterfaceGetter;
            this.m_anonymousGetter = iProxyInterfaceGetter2;
        }

        public IProxyInterfaceGetter getProxyInterface(boolean bl) {
            if (bl) {
                return this.m_anonymousGetter;
            }
            return this.m_dummyGetter;
        }
    }
}

