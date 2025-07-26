#!/bin/sh

# Nginx startup script with configurable access logging
# Set NGINX_ACCESS_LOG environment variable to control access logging:
# - NGINX_ACCESS_LOG=true or NGINX_ACCESS_LOG=/dev/stdout (default) - enable access logging
# - NGINX_ACCESS_LOG=false or NGINX_ACCESS_LOG=off - disable access logging

echo "Starting Nginx with configurable access logging..."

# Set default access log location
if [ -z "$NGINX_ACCESS_LOG" ]; then
    NGINX_ACCESS_LOG="/dev/stdout"
fi

# Handle boolean values
if [ "$NGINX_ACCESS_LOG" = "true" ]; then
    NGINX_ACCESS_LOG="/dev/stdout"
elif [ "$NGINX_ACCESS_LOG" = "false" ]; then
    NGINX_ACCESS_LOG="off"
fi

echo "Access logging set to: $NGINX_ACCESS_LOG"

# Export the variable for envsubst
export NGINX_ACCESS_LOG

# Process the nginx template with access log configuration
if [ "$NGINX_ACCESS_LOG" = "false" ] || [ "$NGINX_ACCESS_LOG" = "off" ]; then
    # Disable access logging
    sed 's|access_log /dev/stdout main;|access_log off;|' /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf
    echo "Access logging disabled"
else
    # Enable access logging (default) - ensure both access and error logs go to stdout/stderr
    sed 's|error_log /var/log/nginx/error.log warn;|error_log /dev/stderr warn;|' /etc/nginx/nginx.conf.template > /etc/nginx/nginx.conf
    echo "Access logging enabled"
fi

# Test nginx configuration
echo "Testing nginx configuration..."
nginx -t

# Start nginx
echo "Starting nginx..."
nginx -g "daemon off;" 