import tensorflow as tf
import time


def run_one(user_dim, item_dim, num_trials=100, device='/gpu'):

    with tf.Graph().as_default():
        users = tf.get_variable(
            'users', shape=[user_dim, item_dim], initializer=tf.random_uniform_initializer())
        items = tf.get_variable(
            'items', shape=[item_dim, item_dim]
        )

        with tf.device(device):
            op = tf.matmul(users, items)

        with tf.Session() as sess:
            sess.run(tf.global_variables_initializer())
            t1 = time.time()

            for t in range(num_trials):
                sess.run(op)
            t2 = time.time()
    return float(t2 - t1) / num_trials


def run_all():
    # 100k doesn't fit on gpu.
    dims = [100, 1000, 10000]  # , 100000, 1000000]

    results = {}
    for d1 in dims:
        for d2 in dims:
            results[(d1, d2)] = run_one(
                d1, d2, num_trials=10
                # device='/cpu'
            )

    print(results)


run_all()
