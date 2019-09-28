package com.casualsuperman.portent;

import java.io.InputStream;
import java.io.OutputStream;

public interface Templater {
	OutputStream rewrite(InputStream is, Context context);
}
