package SalaciousServer.intercepts.interceptify.internal;

import net.bytebuddy.dynamic.ClassFileLocator.Resolution;

class ByteArrayResolution implements Resolution {
  private final byte[] byteCode;

  ByteArrayResolution(byte[] byteCode) {
    this.byteCode = byteCode;
  }

  @Override
  public boolean isResolved() {
    return byteCode != null;
  }

  @Override
  public byte[] resolve() {
    return byteCode;
  }
}
