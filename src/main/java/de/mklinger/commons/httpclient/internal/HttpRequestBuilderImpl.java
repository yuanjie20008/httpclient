package de.mklinger.commons.httpclient.internal;

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import de.mklinger.commons.httpclient.HttpHeaders;
import de.mklinger.commons.httpclient.HttpRequest;
import de.mklinger.commons.httpclient.HttpRequest.BodyProvider;

/**
 * @author Marc Klinger - mklinger[at]mklinger[dot]de
 */
public class HttpRequestBuilderImpl implements HttpRequest.Builder {
	private URI uri;
	private String method = "GET";
	private HttpHeadersImpl headers;
	public HttpRequest.BodyProvider bodyProvider;
	private Duration timeout;

	public HttpRequestBuilderImpl() {
	}

	public HttpRequestBuilderImpl(final URI uri) {
		this.uri = requireNonNull(uri);
	}

	@Override
	public HttpRequest.Builder uri(final URI uri) {
		this.uri = requireNonNull(uri);
		return this;
	}

	@Override
	public HttpRequest.Builder GET() {
		this.method = "GET";
		return this;
	}

	@Override
	public HttpRequest.Builder POST(final BodyProvider bodyProvider) {
		return method("POST", bodyProvider);
	}

	@Override
	public HttpRequest.Builder PUT(final BodyProvider bodyProvider) {
		return method("PUT", bodyProvider);
	}

	@Override
	public HttpRequest.Builder DELETE(final BodyProvider bodyProvider) {
		return method("DELETE", bodyProvider);
	}

	@Override
	public HttpRequest.Builder method(final String method, final BodyProvider bodyProvider) {
		this.method = method;
		this.bodyProvider = requireNonNull(bodyProvider);
		return this;
	}

	@Override
	public HttpRequest.Builder header(final String name, final String value) {
		requireNonNull(name);
		requireNonNull(value);
		// TODO check for valid header: RFC 7230 section-3.2
		if (headers == null) {
			headers = new HttpHeadersImpl();
		}
		headers.addHeader(name, value);
		return this;
	}

	@Override
	public HttpRequest.Builder headers(final String... headers) {
		requireNonNull(headers);
		if (headers.length % 2 != 0) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < headers.length; i += 2) {
			final String name = headers[i];
			final String value = headers[i + 1];
			header(name, value);
		}
		return this;
	}

	@Override
	public HttpRequest.Builder setHeader(final String name, final String value) {
		requireNonNull(name);
		requireNonNull(value);
		if (headers == null) {
			headers = new HttpHeadersImpl();
		}
		headers.setHeader(name, value);
		return header(name, value);
	}

	@Override
	public HttpRequest.Builder timeout(final Duration duration) {
		requireNonNull(duration);
		// TODO check for positive duration
		this.timeout = duration;
		return this;
	}

	@Override
	public HttpRequest build() {
		return new HttpRequestImpl(this);
	}

	private static class HttpRequestImpl implements HttpRequest {
		private final URI uri;
		private final String method;
		private final HttpHeaders headers;
		private final Optional<HttpRequest.BodyProvider> bodyProvider;
		private final Optional<Duration> timeout;

		public HttpRequestImpl(final HttpRequestBuilderImpl builder) {
			this.uri = requireNonNull(builder.uri);
			this.method = requireNonNull(builder.method);
			if (builder.headers == null) {
				this.headers = new HttpHeadersImpl();
			} else {
				this.headers = HttpHeadersImpl.deepCopy(builder.headers);
			}
			this.bodyProvider = Optional.ofNullable(builder.bodyProvider);
			this.timeout = Optional.ofNullable(builder.timeout);
		}

		@Override
		public URI uri() {
			return uri;
		}

		@Override
		public String method() {
			return method;
		}

		@Override
		public HttpHeaders headers() {
			return headers;
		}

		@Override
		public Optional<BodyProvider> bodyProvider() {
			return bodyProvider;
		}

		@Override
		public Optional<Duration> timeout() {
			return timeout;
		}
	}
}
