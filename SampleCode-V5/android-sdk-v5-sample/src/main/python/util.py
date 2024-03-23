import inspect
import functools

def trace_function_call_and_return(func):
    """
    A decorator that prints the function's name, its arguments, and its return value
    when the function is called and returns.
    """
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        # Retrieve the names of the function's arguments
        args_names = inspect.getfullargspec(func).args

        # Match the names with the provided values for positional arguments
        args_str = ', '.join(f"{name}={value}" for name, value in zip(args_names, args))

        # Add the keyword arguments
        kwargs_str = ', '.join(f"{key}={value}" for key, value in kwargs.items())

        # Combine both strings, handling the case when one of them is empty
        args_kwargs_str = ', '.join(filter(None, [args_str, kwargs_str]))

        logging.info(f"Calling {func.__name__}({args_kwargs_str})")

        # Execute the function and get the return value
        result = func(*args, **kwargs)

        # Print the return value
        logging.info(f"{func.__name__} returned {result}")

        return result
    return wrapper


import logging
import os
from functools import wraps

# Define a decorator to automatically capture the function name
def log_function_info(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        logger = logging.getLogger(func.__module__)
        logger.info(f"Calling {func.__name__}")
        return func(*args, **kwargs)
    return wrapper

# Function to set up custom logging
def setup_logging():
    logging.basicConfig(level=logging.INFO,
                        format='python: %(asctime)s - %(filename)s - %(funcName)s - %(message)s',
                        datefmt='%Y-%m-%d %H:%M:%S')

# Example function that uses the logging
@log_function_info
def example_function():
    logging.info("This is an example log message.")

# Setup the logging system
setup_logging()

# Call the example function
example_function()
