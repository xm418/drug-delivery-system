import request from '@/utils/request'

// 登录相关的接口
// 这几个接口原来是用 setTimeout 写的假数据，登录也能"成功"，
// 但是拿到的 token 是瞎编的，后面调别的接口全都 401，
// 所以这里改成真调后端了，token 是后端用 JWT 生成的

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  }).then(res => {
    // 后端返回的字段是平铺的：token、userId、username、realName、roleId、institutionId
    // 这里我把它拼成 store 里要用的 { token, userInfo } 结构，这样 store 那边就不用改了
    if (res.code === 200) {
      const info = res.data
      return {
        code: 200,
        message: res.message,
        data: {
          token: info.token,
          userInfo: {
            id: info.userId,
            username: info.username,
            realName: info.realName,
            roleId: info.roleId,
            institutionId: info.institutionId
          }
        }
      }
    }
    return res
  })
}

// 获取当前登录用户的信息
// 后端对应的接口是 UserController 里的 /users/info
export function getUserInfo() {
  return request({
    url: '/users/info',
    method: 'get'
  })
}

// 登出
// JWT是无状态的，后端没有专门的登出接口，
// 所以这里不请求后台，直接把本地状态清掉就行（清状态在 store 里做的）
export function logout() {
  return Promise.resolve({
    code: 200,
    message: '登出成功'
  })
}
