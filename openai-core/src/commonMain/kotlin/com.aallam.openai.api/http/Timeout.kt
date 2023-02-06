package com.aallam.openai.api.http

import kotlin.time.Duration

/**
 * Http operations timeouts.
 *
 * @param request time period required to process an HTTP call: from sending a request to receiving a response
 * @param connect time period in which a client should establish a connection with a server
 * @param socket maximum time of inactivity between two data packets when exchanging data with a server
 */
public class Timeout(
    public val request: Duration? = null,
    public val connect: Duration? = null,
    public val socket: Duration? = null,
)
