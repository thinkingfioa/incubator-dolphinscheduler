<template>
  <div class="list-model">
    <div class="table-box">
      <table class="fixed">
        <tr>
          <th>
            <span>{{$t('#')}}</span>
          </th>
          <th>
            <span>{{$t('Name')}}</span>
          </th>
          <th>
            <span>{{$t('File Name')}}</span>
          </th>
          <th>
            <span>{{$t('Description')}}</span>
          </th>
          <th width="100">
            <span>{{$t('Size')}}</span>
          </th>
          <th width="140">
            <span>{{$t('Update Time')}}</span>
          </th>
          <th width="140">
            <span>{{$t('Operation')}}</span>
          </th>
        </tr>
        <tr v-for="(item, $index) in list" :key="item.id">
          <td>
            <span>{{parseInt(pageNo === 1 ? ($index + 1) : (($index + 1) + (pageSize * (pageNo - 1))))}}</span>
          </td>
          <td>
            <span class="ellipsis">
              <a href="javascript:" class="links" @click="_go(item)">{{item.alias}}</a>
            </span>
          </td>
          <td><span class="ellipsis">{{item.fileName}}</span></td>
          <td><span class="ellipsis">{{item.desc}}</span></td>
          <td>
            <span>{{_rtSize(item.size)}}</span>
          </td>
          <td>
            <span>{{item.updateTime | formatDate}}</span>
          </td>
          <td>
            <x-button
                    type="info"
                    shape="circle"
                    size="xsmall"
                    data-toggle="tooltip"
                    :title="$t('Edit')"
                    :disabled="_rtDisb(item)"
                    @click="_edit(item,$index)"
                    icon="iconfont icon-bianjixiugai">
            </x-button>
            <x-button
                    type="info"
                    shape="circle"
                    size="xsmall"
                    icon="iconfont icon-wendangxiugai"
                    data-toggle="tooltip"
                    :title="$t('Rename')"
                    @click="_rename(item,$index)">
            </x-button>

            <x-button
                    type="info"
                    shape="circle"
                    size="xsmall"
                    data-toggle="tooltip"
                    :title="$t('Download')"
                    @click="_downloadFile(item)"
                    icon="iconfont icon-download">
            </x-button>

            <x-poptip
                    :ref="'poptip-' + $index"
                    placement="bottom-end"
                    width="90">
              <p>{{$t('Delete?')}}</p>
              <div style="text-align: right; margin: 0;padding-top: 4px;">
                <x-button type="text" size="xsmall" shape="circle" @click="_closeDelete($index)">{{$t('Cancel')}}</x-button>
                <x-button type="primary" size="xsmall" shape="circle" @click="_delete(item,$index)">{{$t('Confirm')}}</x-button>
              </div>
              <template slot="reference">
                <x-button
                        icon="iconfont icon-shanchu"
                        type="error"
                        shape="circle"
                        size="xsmall"
                        data-toggle="tooltip"
                        :title="$t('delete')">
                </x-button>
              </template>
            </x-poptip>
          </td>
        </tr>
      </table>
    </div>
  </div>
</template>
<script>
  import _ from 'lodash'
  import mRename from './rename'
  import { mapActions } from 'vuex'
  import { filtTypeArr } from '../../_source/common'
  import { bytesToSize } from '@/module/util/util'
  import { downloadFile } from '@/module/download'
  import localStore from '@/module/util/localStorage'

  export default {
    name: 'file-manage-list',
    data () {
      return {
        list: []
      }
    },
    props: {
      fileResourcesList: Array,
      pageNo: Number,
      pageSize: Number
    },
    methods: {
      ...mapActions('resource', ['deleteResource']),
      _edit (item) {
        localStore.setItem('file', `${item.alias}|${item.size}`)
        this.$router.push({ path: `/resource/file/edit/${item.id}` })
      },
      _go (item) {
        localStore.setItem('file', `${item.alias}|${item.size}`)
        this.$router.push({ path: `/resource/file/list/${item.id}` })
      },
      _downloadFile (item) {
        downloadFile('/escheduler/resources/download', {
          id: item.id
        })
      },
      _rtSize (val) {
        return bytesToSize(parseInt(val))
      },
      _closeDelete (i) {
        this.$refs[`poptip-${i}`][0].doClose()
      },
      _delete (item, i) {
        this.deleteResource({
          id: item.id
        }).then(res => {
          this.$refs[`poptip-${i}`][0].doClose()
          this.list.splice(i, 1)
          this.$message.success(res.msg)
        }).catch(e => {
          this.$refs[`poptip-${i}`][0].doClose()
          this.$message.error(e.msg || '')
        })
      },
      _rename (item, i) {
        let self = this
        let modal = this.$modal.dialog({
          closable: false,
          showMask: true,
          escClose: true,
          className: 'v-modal-custom',
          transitionName: 'opacityp',
          render (h) {
            return h(mRename, {
              on: {
                onUpDate (item) {
                  self.$set(self.list, i, item)
                  modal.remove()
                },
                close () {
                  modal.remove()
                }
              },
              props: {
                item: item
              }
            })
          }
        })
      },
      _rtDisb ({ alias, size }) {
        let i = alias.lastIndexOf('.')
        let a = alias.substring(i, alias.length)
        let flag = _.includes(filtTypeArr, _.trimStart(a, '.'))
        if (flag && (size < 1000000)) {
          flag = true
        } else {
          flag = false
        }
        return !flag
      }
    },
    watch: {
      fileResourcesList (a) {
        this.list = []
        setTimeout(() => {
          this.list = a
        })
      }
    },
    created () {
    },
    mounted () {
      this.list = this.fileResourcesList
    },
    components: { }
  }
</script>
